CREATE TABLE sales_orders (
    id UUID PRIMARY KEY,
    customer_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(160) NOT NULL,
    updated_by VARCHAR(160) NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    completed_by VARCHAR(160),
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancelled_by VARCHAR(160),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sales_order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    sku VARCHAR(40) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    quantity DECIMAL(19, 3) NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_sales_order_items_order
        FOREIGN KEY (order_id) REFERENCES sales_orders (id),
    CONSTRAINT ck_sales_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_sales_order_items_unit_price CHECK (unit_price > 0),
    CONSTRAINT uk_sales_order_product UNIQUE (order_id, product_id)
);

CREATE INDEX idx_sales_orders_status_created
    ON sales_orders (status, created_at);

CREATE INDEX idx_sales_order_items_order
    ON sales_order_items (order_id);
