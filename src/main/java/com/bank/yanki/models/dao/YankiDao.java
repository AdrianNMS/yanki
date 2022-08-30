package com.bank.yanki.models.dao;

import com.bank.yanki.models.documents.Yanki;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface YankiDao extends ReactiveMongoRepository<Yanki,String>
{

}
