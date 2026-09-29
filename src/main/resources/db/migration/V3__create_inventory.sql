CREATE TABLE stock_items (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    current_quantity DECIMAL(19, 3) NOT NULL,
    minimum_quantity DECIMAL(19, 3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(160) NOT NULL,
    updated_by VARCHAR(160) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_stock_items_current_quantity CHECK (current_quantity >= 0),
    CONSTRAINT ck_stock_items_minimum_quantity CHECK (minimum_quantity >= 0)
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    stock_item_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    quantity DECIMAL(19, 3) NOT NULL,
    resulting_balance DECIMAL(19, 3) NOT NULL,
    reason VARCHAR(250) NOT NULL,
    actor VARCHAR(160) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_stock_movements_item
        FOREIGN KEY (stock_item_id) REFERENCES stock_items (id),
    CONSTRAINT ck_stock_movements_quantity CHECK (quantity > 0),
    CONSTRAINT ck_stock_movements_balance CHECK (resulting_balance >= 0)
);

CREATE INDEX idx_stock_movements_item_date
    ON stock_movements (stock_item_id, occurred_at);
