package com.learning.clearing.config;

import com.learning.clearing.domain.model.Account;
import com.learning.clearing.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        if (accountRepository.count() == 0) {
            log.info("Populando banco de dados com contas de teste...");
            
            Account acc1 = Account.builder()
                    .accountNumber("1234-5")
                    .balance(new BigDecimal("1000.00"))
                    .updatedAt(LocalDateTime.now())
                    .build();

            Account acc2 = Account.builder()
                    .accountNumber("6789-0")
                    .balance(new BigDecimal("500.00"))
                    .updatedAt(LocalDateTime.now())
                    .build();

            Account acc3 = Account.builder()
                    .accountNumber("1111-1")
                    .balance(new BigDecimal("5000.00"))
                    .updatedAt(LocalDateTime.now())
                    .build();

            accountRepository.saveAll(List.of(acc1, acc2, acc3));
            log.info("Banco de dados populado com sucesso!");
        } else {
            log.info("Banco de dados já contém dados, pulando seed.");
        }
    }
}
