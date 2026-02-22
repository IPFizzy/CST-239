package model;

import java.math.BigDecimal;

/**
 * Represents an Armor item in the store.
 * Armor is sold like a normal product, but the type helps organize inventory.
 */
public class Armor extends SalableProduct {

    /**
     * Creates an Armor item with standard product fields.
     */
    public Armor(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
