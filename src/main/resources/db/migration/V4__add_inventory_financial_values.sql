ALTER TABLE stock_items
    ADD COLUMN average_unit_cost DECIMAL(19, 4) DEFAULT 0 NOT NULL;

ALTER TABLE stock_movements
    ADD COLUMN unit_price DECIMAL(19, 2);

ALTER TABLE stock_movements
    ADD COLUMN unit_cost DECIMAL(19, 4) DEFAULT 0 NOT NULL;

ALTER TABLE stock_items
    ADD CONSTRAINT ck_stock_items_average_cost CHECK (average_unit_cost >= 0);

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_stock_movements_unit_price CHECK (unit_price IS NULL OR unit_price > 0);

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_stock_movements_unit_cost CHECK (unit_cost >= 0);
