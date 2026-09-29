package br.com.pasteldahora.order.application.service;

import br.com.pasteldahora.catalog.application.port.in.CatalogItemQuery;
import br.com.pasteldahora.catalog.application.port.in.SellableProductData;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.inventory.application.port.in.InventorySalesPort;
import br.com.pasteldahora.inventory.application.port.in.RegisterSaleCommand;
import br.com.pasteldahora.order.application.port.in.AddOrderItemCommand;
import br.com.pasteldahora.order.application.port.in.CreateOrderCommand;
import br.com.pasteldahora.order.application.port.in.OrderUseCase;
import br.com.pasteldahora.order.application.port.in.OrderFinancialData;
import br.com.pasteldahora.order.application.port.in.OrderFinancialQuery;
import br.com.pasteldahora.order.application.port.out.OrderRepositoryPort;
import br.com.pasteldahora.order.domain.exception.OrderNotFoundException;
import br.com.pasteldahora.order.domain.model.Order;
import br.com.pasteldahora.order.domain.model.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderApplicationService implements OrderUseCase, OrderFinancialQuery {

    private final OrderRepositoryPort orderRepository;
    private final CatalogItemQuery catalogItemQuery;
    private final InventorySalesPort inventorySalesPort;
    private final Clock clock;

    public OrderApplicationService(
            OrderRepositoryPort orderRepository,
            CatalogItemQuery catalogItemQuery,
            InventorySalesPort inventorySalesPort,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.catalogItemQuery = catalogItemQuery;
        this.inventorySalesPort = inventorySalesPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Order create(CreateOrderCommand command) {
        return orderRepository.save(Order.create(
                command.customerId(),
                command.actor(),
                Instant.now(clock)
        ));
    }

    @Override
    @Transactional
    public Order addItem(AddOrderItemCommand command) {
        Order order = findById(command.orderId());
        SellableProductData product = catalogItemQuery.findSellableProduct(command.productId());
        if (!product.active()) {
            throw new IllegalArgumentException("O produto informado está inativo.");
        }
        Order updated = order.addItem(
                product.id(),
                product.sku(),
                product.name(),
                command.quantity(),
                product.salePrice(),
                command.actor(),
                Instant.now(clock)
        );
        return orderRepository.save(updated);
    }

    @Override
    @Transactional
    public Order removeItem(UUID orderId, UUID itemId, String actor) {
        return orderRepository.save(
                findById(orderId).removeItem(itemId, actor, Instant.now(clock))
        );
    }

    @Override
    @Transactional
    public Order complete(UUID orderId, String actor) {
        Order order = findById(orderId);
        for (var item : order.getItems()) {
            SellableProductData product = catalogItemQuery.findSellableProduct(item.getProductId());
            if (product.inventoryPolicy() == InventoryPolicy.DIRECT_STOCK) {
                inventorySalesPort.registerSale(new RegisterSaleCommand(
                        item.getProductId(),
                        order.getId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        actor
                ));
            }
        }
        return orderRepository.save(order.complete(actor, Instant.now(clock)));
    }

    @Override
    @Transactional
    public Order cancel(UUID orderId, String actor) {
        return orderRepository.save(
                findById(orderId).cancel(actor, Instant.now(clock))
        );
    }

    @Override
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<SellableProductData> findProductsAvailableForSale() {
        return catalogItemQuery.findActiveSellableProducts();
    }

    @Override
    public OrderFinancialData getFinancialData() {
        return new OrderFinancialData(
                orderRepository.findAll().stream()
                        .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                        .map(Order::getTotalAmount)
                        .reduce(
                                java.math.BigDecimal.ZERO.setScale(2),
                                java.math.BigDecimal::add
                        )
        );
    }
}
