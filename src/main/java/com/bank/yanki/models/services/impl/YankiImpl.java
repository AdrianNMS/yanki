package com.bank.yanki.models.services.impl;

import com.bank.yanki.models.dao.YankiDao;
import com.bank.yanki.models.documents.Yanki;
import com.bank.yanki.models.services.YankiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class YankiImpl implements YankiService
{
    @Autowired
    private YankiDao dao;

    @Autowired
    private ReactiveRedisTemplate<String, Yanki> redisTemplate;
    @Override
    public Mono<List<Yanki>> findAll() {
        return dao.findAll()
                .collectList();
    }

    @Override
    public Mono<Yanki> find(String id) {
        return redisTemplate.opsForValue().get(id)
                .switchIfEmpty(dao.findById(id)
                        .doOnNext(yan -> redisTemplate.opsForValue()
                                .set(yan.getId(), yan)
                                .subscribe(aBoolean -> {
                                    redisTemplate.expire(id, Duration.ofMinutes(10)).subscribe();
                                })));
    }

    @Override
    public Mono<Yanki> create(Yanki yanki) {
        return dao.save(yanki)
                .doOnNext(yan -> redisTemplate.opsForValue()
                        .set(yan.getId(), yan)
                        .subscribe(aBoolean -> {
                            redisTemplate.expire(yan.getId(), Duration.ofMinutes(10)).subscribe();
                        }));
    }

    @Override
    public Mono<Yanki> update(String id, Yanki yanki) {
        return dao.existsById(id).flatMap(check ->
        {
            if (Boolean.TRUE.equals(check))
            {
                redisTemplate.opsForValue().delete(id).subscribe();
                return dao.save(yanki)
                        .doOnNext(yan -> redisTemplate.opsForValue()
                                .set(yan.getId(), yan)
                                .subscribe(aBoolean -> {
                                    redisTemplate.expire(id, Duration.ofMinutes(10));
                                }));
            }
            else
                return Mono.empty();

        });
    }

    @Override
    public Mono<Object> delete(String id) {
        return dao.existsById(id).flatMap(check -> {
            if (Boolean.TRUE.equals(check))
            {
                redisTemplate.opsForValue().delete(id).subscribe();
                return dao.deleteById(id).then(Mono.just(true));
            }
            else
                return Mono.empty();
        });
    }

    @Override
    public Mono<Yanki> updateMont(String id, Float mont) {
        return dao.findById(id).flatMap(yanki -> {
            yanki.setMont(yanki.getMont() + mont);
            return dao.save(yanki);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Yanki> findByPhoneNumber(String phoneNumber) {
        return findAll().flatMap(yankis ->
            Mono.just(Objects.requireNonNull(yankis.stream().filter(yanki -> yanki.getPhoneNumber().equals(phoneNumber)).findFirst().orElse(null)))
        );
    }
}
