package com.epgpay.mongoquerypoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HeaderRepositoryITest {

    private static final long CUSTOMER_ID = 1L;
    private static final String EXPECTED_CUSTOMER_NAME = "Alan Turing";
    @Inject
    private HeaderRepository invoiceRepository;

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {

        invoiceRepository.deleteAll();
        final List<HeaderModel> invoices = createRandomInvoice(20);
        invoiceRepository.saveAll(invoices);

        customerRepository.save(CustomerModel.builder()
                .id(CUSTOMER_ID)
                .name(EXPECTED_CUSTOMER_NAME)
                .build());
    }

    @Ignore
    @Test
    public void should_insert20_when_setup() {
        //when
        final Integer invoices = invoiceRepository.findAll().size();

        //then
        assertThat(invoices).isEqualTo(20);
    }

    @Test
    public void should_have4PagesOf5Records_when_PageSizeIs5() {
        //given
        final List<List<HeaderModel>> pages = new LinkedList<>();

        //when
        for (int pageNumber = 0; pageNumber < 4; pageNumber++) {
            final Pageable pageable = PageRequest.of(pageNumber, 5);
            final Query query = new Query().with(pageable);
            final List<HeaderModel> invoices = mongoTemplate.find(query, HeaderModel.class);
            pages.add(invoices);

        }

        //then
        assertThat(pages).hasSize(4);
    }

    @Test
    public void should_returnOnlyInvoicesCloseToNow_when_DueDateFilterIsAdded() {
        //given
        final HeaderModel invoiceExpiredYesterday = HeaderModel.builder()
                .customerId(CUSTOMER_ID)
                .dueDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .status(HeaderStatus.DRAFT)
                .code("test code")
                .summary("this invoice has been expired in now")
                .build();

        final HeaderModel invoiceExpiredTwoDaysAgo = HeaderModel.builder()
                .customerId(CUSTOMER_ID)
                .dueDate(Instant.now().minus(2, ChronoUnit.DAYS))
                .code("expired test code")
                .status(HeaderStatus.DRAFT)
                .summary("this invoice has been expired in now")
                .build();

        final List<HeaderModel> expiredInvoices = Arrays.asList(invoiceExpiredYesterday, invoiceExpiredTwoDaysAgo);
        invoiceRepository.saveAll(expiredInvoices);

        final Query query = new Query()
                .addCriteria(Criteria.where("due_date").gt(Instant.now().minus(3, ChronoUnit.DAYS)).lt(Instant.now().minus(1, ChronoUnit.DAYS)));

        //when
        final List<HeaderModel> invoices = mongoTemplate.find(query, HeaderModel.class);

        //then
        assertThat(invoices).hasSize(2).containsExactly(invoiceExpiredYesterday, invoiceExpiredTwoDaysAgo);
    }

    @Test
    public void should_returnInvoiceByCode_when_CodeFilterIsAdded() {
        //given

        final Query query = new Query()
                .addCriteria(Criteria.where("code").is("code:1"));

        //when
        final List<HeaderModel> invoices = mongoTemplate.find(query, HeaderModel.class);


        //then
        assertThat(invoices).hasSize(1).extracting("code").containsOnlyOnce("code:1");
    }

    @Test
    public void should_retrieveCustomer_when_Join() {
        //given
        final List<AggregationOperation> aggregationOperations = new LinkedList<>();
        final LookupOperation lookupForHeader = LookupOperation.newLookup().
                from("customers").
                localField("customer_id").
                foreignField("_id").
                as("customer");
        aggregationOperations.add(lookupForHeader);
        final Aggregation aggregatedQuery = newAggregation(aggregationOperations);

        //when
        final List<DetailedHeaderModel> detailedInvoices = mongoTemplate.aggregate(aggregatedQuery, DetailedHeaderModel.class, DetailedHeaderModel.class).getMappedResults();

        //then
        assertThat(detailedInvoices).extracting("customer.id", "customer.name").containsOnly(tuple(CUSTOMER_ID, EXPECTED_CUSTOMER_NAME));
    }

    private List<HeaderModel> createRandomInvoice(final Integer maxInvoices) {

        final List<HeaderModel> invoices = new LinkedList<>();

        for (int i = 0; i < maxInvoices; i++) {
            invoices.add(
                    HeaderModel.builder()
                            .customerId(CUSTOMER_ID)
                            .dueDate(Instant.now())
                            .code("code:" + i)
                            .status(HeaderStatus.DRAFT)
                            .summary("this is a mock summary" + i)
                            .build());
        }

        return invoices;
    }

}