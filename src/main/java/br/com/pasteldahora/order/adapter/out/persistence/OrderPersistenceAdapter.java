package br.com.pasteldahora.order.adapter.out.persistence;

import br.com.pasteldahora.order.application.port.out.OrderRepositoryPort;
import br.com.pasteldahora.order.domain.model.Order;
import br.com.pasteldahora.order.domain.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;

    OrderPersistenceAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return toDomain(repository.save(toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderPersistenceAdapter::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(OrderPersistenceAdapter::toDomain)
                .toList();
    }

    private static OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getCreatedBy(),
                order.getUpdatedBy(),
                order.getCompletedAt(),
                order.getCompletedBy(),
                order.getCancelledAt(),
                order.getCancelledBy(),
                order.getVersion()
        );
        entity.replaceItems(order.getItems().stream()
                .map(item -> toItemEntity(entity, item))
                .toList());
        return entity;
    }

    private static OrderItemJpaEntity toItemEntity(
            OrderJpaEntity order,
            OrderItem item
    ) {
        return new OrderItemJpaEntity(
                item.getId(),
                order,
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    private static Order toDomain(OrderJpaEntity entity) {
        return Order.restore(
                entity.getId(),
                entity.getCustomerId(),
                entity.getItems().stream()
                        .map(OrderPersistenceAdapter::toItemDomain)
                        .toList(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCompletedAt(),
                entity.getCompletedBy(),
                entity.getCancelledAt(),
                entity.getCancelledBy(),
                entity.getVersion()
        );
    }

    private static OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return OrderItem.restore(
                entity.getId(),
                entity.getProductId(),
                entity.getSku(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }
}
