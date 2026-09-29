CREATE TABLE ingredients (
    id UUID PRIMARY KEY,
    sku VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    stock_unit VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(160) NOT NULL,
    updated_by VARCHAR(160) NOT NULL,
    deactivated_at TIMESTAMP WITH TIME ZONE,
    deactivated_by VARCHAR(160),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_ingredients_active ON ingredients (active);
CREATE INDEX idx_ingredients_name ON ingredients (name);

ALTER TABLE products
    ADD COLUMN customizable BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE product_ingredients (
    product_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    quantity DECIMAL(19, 3) NOT NULL,
    CONSTRAINT pk_product_ingredients PRIMARY KEY (product_id, ingredient_id),
    CONSTRAINT fk_product_ingredients_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_ingredients_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients (id),
    CONSTRAINT ck_product_ingredients_quantity CHECK (quantity > 0)
);

ALTER TABLE stock_items RENAME COLUMN product_id TO item_id;
ALTER TABLE stock_items
    ADD COLUMN item_type VARCHAR(20) NOT NULL DEFAULT 'PRODUCT';

CREATE INDEX idx_stock_items_type ON stock_items (item_type);
