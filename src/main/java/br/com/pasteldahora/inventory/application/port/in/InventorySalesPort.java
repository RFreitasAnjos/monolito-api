package br.com.pasteldahora.inventory.application.port.in;

public interface InventorySalesPort {

    void registerSale(RegisterSaleCommand command);
}
