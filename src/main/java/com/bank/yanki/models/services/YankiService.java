package com.bank.yanki.models.services;

import com.bank.yanki.models.documents.Yanki;
import reactor.core.publisher.Mono;

import java.util.List;

public interface YankiService
{
    Mono<List<Yanki>> findAll();
    Mono<Yanki> find(String id);
    Mono<Yanki> create(Yanki yanki);
    Mono<Yanki> update(String id, Yanki yanki);
    Mono<Object> delete(String id);
}
