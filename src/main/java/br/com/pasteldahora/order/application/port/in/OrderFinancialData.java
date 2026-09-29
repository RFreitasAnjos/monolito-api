package br.com.pasteldahora.order.application.port.in;

import java.math.BigDecimal;

public record OrderFinancialData(BigDecimal salesRevenue) {
}
