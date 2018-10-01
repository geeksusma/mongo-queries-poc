package com.epgpay.mongoquerypoc;

import org.springframework.data.mongodb.repository.MongoRepository;

interface HeaderRepository extends MongoRepository<HeaderModel, String> {
}
