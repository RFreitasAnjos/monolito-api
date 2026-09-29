package br.com.pasteldahora.order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {

    private final UUID id;
    private final UUID customerId;
    private final List<OrderItem> items;
    private final OrderStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant completedAt;
    private final String completedBy;
    private final Instant cancelledAt;
    private final String cancelledBy;
    private final long version;

    private Order(
            UUID id,
            UUID customerId,
            List<OrderItem> items,
            OrderStatus status,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant completedAt,
            String completedBy,
            Instant cancelledAt,
            String cancelledBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do pedido é obrigatório.");
        this.customerId = customerId;
        this.items = List.copyOf(Objects.requireNonNull(items, "Os itens são obrigatórios."));
        this.status = Objects.requireNonNull(status, "O status do pedido é obrigatório.");
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateActor(createdBy);
        this.updatedBy = validateActor(updatedBy);
        this.completedAt = completedAt;
        this.completedBy = completedBy;
        this.cancelledAt = cancelledAt;
        this.cancelledBy = cancelledBy;
        validateLifecycle();
        if (version < 0) {
            throw new IllegalArgumentException("A versão do pedido não pode ser negativa.");
        }
        this.version = version;
    }

    public static Order create(UUID customerId, String actor, Instant now) {
        return new Order(
                UUID.randomUUID(),
                customerId,
                List.of(),
                OrderStatus.OPEN,
                now,
                now,
                actor,
                actor,
                null,
                null,
                null,
                null,
                0
        );
    }

    public static Order restore(
            UUID id,
            UUID customerId,
            List<OrderItem> items,
            OrderStatus status,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant completedAt,
            String completedBy,
            Instant cancelledAt,
            String cancelledBy,
            long version
    ) {
        return new Order(
                id,
                customerId,
                items,
                status,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                completedAt,
                completedBy,
                cancelledAt,
                cancelledBy,
                version
        );
    }

    public Order addItem(
            UUID productId,
            String sku,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String actor,
            Instant now
    ) {
        requireOpen();
        List<OrderItem> updatedItems = new ArrayList<>(items);
        int existingIndex = findItemIndexByProductId(productId);
        if (existingIndex >= 0) {
            updatedItems.set(existingIndex, updatedItems.get(existingIndex).increase(quantity));
        } else {
            updatedItems.add(OrderItem.create(
                    productId,
                    sku,
                    productName,
                    quantity,
                    unitPrice
            ));
        }
        return copy(updatedItems, status, actor, now, completedAt, completedBy, null, null);
    }

    public Order removeItem(UUID itemId, String actor, Instant now) {
        requireOpen();
        List<OrderItem> updatedItems = items.stream()
                .filter(item -> !item.getId().equals(itemId))
                .toList();
        if (updatedItems.size() == items.size()) {
            throw new IllegalArgumentException("O item informado não pertence ao pedido.");
        }
        return copy(updatedItems, status, actor, now, completedAt, completedBy, null, null);
    }

    public Order complete(String actor, Instant now) {
        requireOpen();
        if (items.isEmpty()) {
            throw new IllegalStateException("O pedido precisa possuir ao menos um item.");
        }
        return copy(items, OrderStatus.COMPLETED, actor, now, now, actor, null, null);
    }

    public Order cancel(String actor, Instant now) {
        requireOpen();
        return copy(items, OrderStatus.CANCELLED, actor, now, null, null, now, actor);
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    private Order copy(
            List<OrderItem> updatedItems,
            OrderStatus updatedStatus,
            String actor,
            Instant now,
            Instant newCompletedAt,
            String newCompletedBy,
            Instant newCancelledAt,
            String newCancelledBy
    ) {
        return new Order(
                id,
                customerId,
                updatedItems,
                updatedStatus,
                createdAt,
                now,
                createdBy,
                actor,
                newCompletedAt,
                newCompletedBy,
                newCancelledAt,
                newCancelledBy,
                version
        );
    }

    private int findItemIndexByProductId(UUID productId) {
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getProductId().equals(productId)) {
                return index;
            }
        }
        return -1;
    }

    private void requireOpen() {
        if (status != OrderStatus.OPEN) {
            throw new IllegalStateException("Somente pedidos abertos podem ser alterados.");
        }
    }

    private void validateLifecycle() {
        if (status == OrderStatus.OPEN) {
            if (completedAt != null || completedBy != null || cancelledAt != null || cancelledBy != null) {
                throw new IllegalArgumentException("Pedido aberto não pode possuir dados de finalização.");
            }
            return;
        }
        if (status == OrderStatus.COMPLETED) {
            Objects.requireNonNull(completedAt, "A data de conclusão é obrigatória.");
            validateActor(completedBy);
            if (cancelledAt != null || cancelledBy != null) {
                throw new IllegalArgumentException("Pedido concluído não pode estar cancelado.");
            }
            return;
        }
        Objects.requireNonNull(cancelledAt, "A data de cancelamento é obrigatória.");
        validateActor(cancelledBy);
        if (completedAt != null || completedBy != null) {
            throw new IllegalArgumentException("Pedido cancelado não pode estar concluído.");
        }
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A atualização não pode ser anterior à criação.");
        }
        return updatedAt;
    }

    private static String validateActor(String actor) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("O responsável pelo pedido é obrigatório.");
        }
        String normalized = actor.trim();
        if (normalized.length() > 160) {
            throw new IllegalArgumentException(
                    "O responsável deve possuir no máximo 160 caracteres."
            );
        }
        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public long getVersion() {
        return version;
    }
}
