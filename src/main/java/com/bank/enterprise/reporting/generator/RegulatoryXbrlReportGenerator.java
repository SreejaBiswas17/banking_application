package com.bank.enterprise.reporting.generator;

import com.bank.enterprise.dto.ReportDto;
import org.springframework.stereotype.Component;

@Component
public class RegulatoryXbrlReportGenerator {

    public String generateXbrlXml(ReportDto.FinancialSummaryDto summary) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:bank=\"http://bank.enterprise.com/2026/fin\">\n");
        xml.append("  <xbrli:context id=\"FY2026\">\n");
        xml.append("    <xbrli:entity>\n");
        xml.append("      <xbrli:identifier scheme=\"http://www.sec.gov/CIK\">0001234567</xbrli:identifier>\n");
        xml.append("    </xbrli:entity>\n");
        xml.append("  </xbrli:context>\n");
        xml.append("  <bank:TotalCustomers contextRef=\"FY2026\">").append(summary.getTotalCustomers()).append("</bank:TotalCustomers>\n");
        xml.append("  <bank:TotalAccounts contextRef=\"FY2026\">").append(summary.getTotalAccounts()).append("</bank:TotalAccounts>\n");
        xml.append("  <bank:TotalDeposits contextRef=\"FY2026\">").append(summary.getTotalBankDeposits()).append("</bank:TotalDeposits>\n");
        xml.append("  <bank:TotalLoansDisbursed contextRef=\"FY2026\">").append(summary.getTotalLoansDisbursed()).append("</bank:TotalLoansDisbursed>\n");
        xml.append("  <bank:TotalOutstandingLoans contextRef=\"FY2026\">").append(summary.getTotalOutstandingLoans()).append("</bank:TotalOutstandingLoans>\n");
        xml.append("</xbrli:xbrl>");
        return xml.toString();
    }
}
