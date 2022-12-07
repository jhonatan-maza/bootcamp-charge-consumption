package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.LoadBalance;
import com.nttdata.bootcamp.repository.LoadBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class LoadBalanceServiceImpl implements LoadBalanceService {
    @Autowired
    private LoadBalanceRepository loadBalanceRepository;

    @Override
    public Flux<LoadBalance> findAll() {
        Flux<LoadBalance> loadBalanceFlux = loadBalanceRepository.findAll();
        return loadBalanceFlux;
    }

    @Override
    public Flux<LoadBalance> findByAccountNumber(String accountNumber) {
        Flux<LoadBalance> loadBalanceFlux = loadBalanceRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return loadBalanceFlux;
    }

    @Override
    public Mono<LoadBalance> findByNumber(String Number) {
        Mono<LoadBalance> loadBalanceMono = loadBalanceRepository
                .findAll()
                .filter(x -> x.getLoadBalanceNumber().equals(Number))
                .next();
        return loadBalanceMono;
    }

    @Override
    public Mono<LoadBalance> saveLoadBalance(LoadBalance dataLoadBalance) {
        Mono<LoadBalance> loadBalanceMono = findByNumber(dataLoadBalance.getLoadBalanceNumber())
                .flatMap(__ -> Mono.<LoadBalance>error(new Error("This loadBalance  number " + dataLoadBalance.getLoadBalanceNumber() + "exists")))
                .switchIfEmpty(loadBalanceRepository.save(dataLoadBalance));
        return loadBalanceMono;


    }

    @Override
    public Mono<LoadBalance> updateLoadBalance(LoadBalance dataLoadBalance) {

        Mono<LoadBalance> transactionMono = findByNumber(dataLoadBalance.getLoadBalanceNumber());
        try {
            dataLoadBalance.setDni(transactionMono.block().getDni());
            dataLoadBalance.setAmount(transactionMono.block().getAmount());
            dataLoadBalance.setCreationDate(transactionMono.block().getCreationDate());
            return loadBalanceRepository.save(dataLoadBalance);
        }catch (Exception e){
            return Mono.<LoadBalance>error(new Error("This loadBalance  " + dataLoadBalance.getAccountNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteLoadBalance(String Number) {
        Mono<LoadBalance> loadBalanceMono = findByNumber(Number);
        try {
            LoadBalance loadBalance = loadBalanceMono.block();
            return loadBalanceRepository.delete(loadBalance);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("This deposits number" + Number+ " do not exists"));
        }
    }





}
