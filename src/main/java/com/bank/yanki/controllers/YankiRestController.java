package com.bank.yanki.controllers;

import com.bank.yanki.handler.ResponseHandler;
import com.bank.yanki.models.documents.Yanki;
import com.bank.yanki.models.enums.TransferenceType;
import com.bank.yanki.models.kafka.RequestYanki;
import com.bank.yanki.models.kafka.ResponseTransference;
import com.bank.yanki.models.services.YankiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/yanki")
public class YankiRestController
{
    @Autowired
    private YankiService yankiService;

    @Autowired
    private KafkaTemplate<String, ResponseTransference> template;

    private static final Logger log = LoggerFactory.getLogger(YankiRestController.class);

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@Validated @RequestBody Yanki yan) {
        log.info("[INI] create");

        return yankiService.create(yan)
                .doOnNext(wallet -> log.info(wallet.toString()))
                .flatMap(wallet -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, wallet)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create"));
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll() {
        log.info("[INI] findAll");

        return yankiService.findAll()
                .doOnNext(wallets -> log.info(wallets.toString()))
                .flatMap(wallets -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, wallets)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll"));

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id) {
        log.info("[INI] find");

        return yankiService.find(id)
                .doOnNext(wallet -> log.info(wallet.toString()))
                .map(wallet -> ResponseHandler.response("Done", HttpStatus.OK, wallet))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id,@Validated @RequestBody Yanki yan) {
        log.info("[INI] update");

        return yankiService.update(id,yan)
                .flatMap(wallet -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, wallet)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] update"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id) {
        log.info("[INI] delete");

        return yankiService.delete(id)
                .flatMap(o -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Error", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] delete"));
    }

    @KafkaListener(topics = "yanki-check", groupId = "yanki")
    public void receiveCheckMont(@Payload RequestYanki requestYanki)
    {
        log.info("[INI] receiveCheckMont");

        var userCheck = (requestYanki.getTransferenceType() == TransferenceType.BUY)
                ? requestYanki.getPhoneNumberSender() : requestYanki.getPhoneNumberReceiver();

        log.info(userCheck);

        yankiService.findByPhoneNumber(userCheck).subscribe(yanki -> {
            var status = (yanki!=null && yanki.getMont()>=requestYanki.getMont());

            var response = ResponseTransference.builder()
                    .idTransference(requestYanki.getIdTransference())
                    .status(status)
                    .build();

            log.info(response.toString());

            template.send("transference_yanki-check",response);

            log.info("[END] receiveCheckMont");

        });
    }

    @KafkaListener(topics = "yanki-update", groupId = "yanki")
    public void receiveUpdateMonts(@Payload RequestYanki requestYanki)
    {
        log.info("[INI] receiveUpdateMonts");

        var user1Check = (requestYanki.getTransferenceType() == TransferenceType.BUY)
                ? requestYanki.getPhoneNumberSender() : requestYanki.getPhoneNumberReceiver();
        var user2Check = (requestYanki.getTransferenceType() == TransferenceType.BUY)
                ? requestYanki.getPhoneNumberReceiver() : requestYanki.getPhoneNumberSender();

        log.info(user1Check);
        log.info(user2Check);

        yankiService.updateMont(user1Check, -requestYanki.getMont())
                .subscribe(yanki -> {

                    log.info(yanki.toString());

                    if(yanki!=null)
                        yankiService.updateMont(user2Check, requestYanki.getMont())
                                .subscribe(yanki1 -> {
                                    log.info(yanki1.toString());
                                    template.send("transference_yanki-update",ResponseTransference.builder()
                                            .idTransference(requestYanki.getIdTransference())
                                            .status((yanki1!=null))
                                            .build());
                                });
                    else
                        template.send("transference_yanki-update",ResponseTransference.builder()
                                .idTransference(requestYanki.getIdTransference())
                                .status(false)
                                .build());

                    log.info("[END] receiveUpdateMonts");
                });
    }
}
