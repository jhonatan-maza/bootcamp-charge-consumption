package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.LoadBalance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface LoadBalanceService {

    public Flux<LoadBalance> findAll();
    public Flux<LoadBalance> findByAccountNumber(String accountNumber);

    public Mono<LoadBalance> findByNumber(String number);
    public Mono<LoadBalance> saveLoadBalance(LoadBalance balance);
    public Mono<LoadBalance> updateLoadBalance(LoadBalance balance);
    public Mono<Void> deleteLoadBalance(String accountNumber);

}
