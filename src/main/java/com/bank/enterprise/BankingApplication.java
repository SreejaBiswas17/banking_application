package com.bank.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Enterprise Banking Core Engine Application Entry Point.
 * Configured with Oracle DB Data Access, Multi-tier Domain Modules,
 * Role-Based Access Control Security, and Comprehensive Audit Trails.
 *
 * @author DeepMind AI Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
