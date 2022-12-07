package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.LoadBalance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface LoadBalanceRepository extends ReactiveCrudRepository<LoadBalance, String> {
}
