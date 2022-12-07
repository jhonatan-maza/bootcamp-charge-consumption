package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.LoadBalance;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.LoadBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/loadBalance")
public class LoadBalanceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalanceController.class);
	@Autowired
	private LoadBalanceService loadBalanceService;

	//LoadBalance search
	@GetMapping("/findAllLoadBalance")
	public Flux<LoadBalance> findAllLoadBalance() {
		Flux<LoadBalance> deposits = loadBalanceService.findAll();
		LOGGER.info("Registered loadBalance: " + deposits);
		return deposits;
	}

	//LoadBalance by AccountNumber
	@GetMapping("/findAllLoadBalanceByAccountNumber/{accountNumber}")
	public Flux<LoadBalance> findAllLoadBalanceByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<LoadBalance> loadBalanceFlux = loadBalanceService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered LoadBalance of account number: "+accountNumber +"-" + loadBalanceFlux);
		return loadBalanceFlux;
	}

	//LoadBalance  by Number
	@CircuitBreaker(name = "loadBalance", fallbackMethod = "fallBackGetLoadBalance")
	@GetMapping("/findByLoadBalanceNumber/{numberDeposits}")
	public Mono<LoadBalance> findByLoadBalanceNumber(@PathVariable("numberDeposits") String numberDeposits) {
		LOGGER.info("Searching deposits by number: " + numberDeposits);
		return loadBalanceService.findByNumber(numberDeposits);
	}

	//Save LoadBalance
	@CircuitBreaker(name = "loadBalance", fallbackMethod = "fallBackGetLoadBalance")
	@PostMapping(value = "/saveLoadBalance")
	public Mono<LoadBalance> saveLoadBalance(@RequestBody LoadBalance dataLoadBalance){
		Mono.just(dataLoadBalance).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataLoadBalance).onErrorResume(e -> Mono.just(dataLoadBalance))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<LoadBalance> loadBalanceMono = loadBalanceService.saveLoadBalance(dataLoadBalance);
		return loadBalanceMono;
	}

	//Update LoadBalance
	@CircuitBreaker(name = "loadBalance", fallbackMethod = "fallBackGetLoadBalance")
	@PutMapping("/updateLoadBalance/{numberTransaction}")
	public Mono<LoadBalance> updateLoadBalance(@PathVariable("numberTransaction") String numberTransaction,
										   @Valid @RequestBody LoadBalance dataLoadBalance) {
		Mono.just(dataLoadBalance).doOnNext(t -> {

					t.setLoadBalanceNumber(numberTransaction);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataLoadBalance).onErrorResume(e -> Mono.just(dataLoadBalance))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<LoadBalance> updateDeposit = loadBalanceService.updateLoadBalance(dataLoadBalance);
		return updateDeposit;
	}


	//Delete LoadBalance
	@CircuitBreaker(name = "loadBalance", fallbackMethod = "fallBackGetLoadBalance")
	@DeleteMapping("/deleteLoadBalance/{numberTransaction}")
	public Mono<Void> deleteLoadBalance(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting loadBalance by number: " + numberTransaction);
		Mono<Void> delete = loadBalanceService.deleteLoadBalance(numberTransaction);
		return delete;

	}


	@GetMapping("/getCountDeposits/{accountNumber}")
	//get count of LoadBalance
	public Mono<Long> getCountDeposits(@PathVariable("accountNumber") String accountNumber){
		Flux<LoadBalance> transactions= findAllLoadBalanceByAccountNumber(accountNumber);
		return transactions.count();
	}


	private Mono<LoadBalance> fallBackGetDeposits(Exception e){
		LoadBalance loadBalance = new LoadBalance();
		Mono<LoadBalance> loadBalanceMono= Mono.just(loadBalance);
		return loadBalanceMono;
	}


}
