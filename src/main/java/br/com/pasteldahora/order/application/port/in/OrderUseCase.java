package br.com.pasteldahora.order.application.port.in;

import br.com.pasteldahora.catalog.application.port.in.SellableProductData;
import br.com.pasteldahora.order.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {

    Order create(CreateOrderCommand command);

    Order addItem(AddOrderItemCommand command);

    Order removeItem(UUID orderId, UUID itemId, String actor);

    Order complete(UUID orderId, String actor);

    Order cancel(UUID orderId, String actor);

    Order findById(UUID orderId);

    List<Order> findAll();

    List<SellableProductData> findProductsAvailableForSale();
}
