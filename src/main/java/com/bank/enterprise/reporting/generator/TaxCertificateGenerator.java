package com.bank.enterprise.reporting.generator;

import com.bank.enterprise.dto.CustomerDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TaxCertificateGenerator {

    public String generateAnnualTaxCertificate(CustomerDto.CustomerResponse customer, int financialYear, BigDecimal totalInterestEarned, BigDecimal totalTdsDeducted) {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append("                  ENTERPRISE BANKING SYSTEM                            \n");
        sb.append("               ANNUAL TAX DEDUCTION CERTIFICATE                        \n");
        sb.append("=======================================================================\n");
        sb.append("Financial Year: ").append(financialYear).append("-").append(financialYear + 1).append("\n");
        sb.append("Customer Name : ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
        sb.append("Tax ID / PAN  : ").append(customer.getTaxIdNumberMasked()).append("\n");
        sb.append("Address       : ").append(customer.getAddressLine1()).append(", ").append(customer.getCity()).append("\n");
        sb.append("-----------------------------------------------------------------------\n");
        sb.append("Total Interest Credited : USD ").append(totalInterestEarned).append("\n");
        sb.append("Total Tax Deducted (TDS): USD ").append(totalTdsDeducted).append("\n");
        sb.append("Net Interest Amount     : USD ").append(totalInterestEarned.subtract(totalTdsDeducted)).append("\n");
        sb.append("-----------------------------------------------------------------------\n");
        sb.append("Generated on: ").append(LocalDate.now()).append(" by Automated Banking Core Engine\n");
        sb.append("=======================================================================\n");
        return sb.toString();
    }
}
