package com.epgpay.mongoquerypoc;

import org.springframework.data.mongodb.repository.MongoRepository;

interface CustomerRepository extends MongoRepository<CustomerModel, Long> {
}
