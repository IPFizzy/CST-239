package model;

import java.math.BigDecimal;

/**
 * Represents a Health item in the store (like potions).
 * Keeping it as a class makes it clear this is not a weapon or armor.
 */
public class Health extends SalableProduct {

    /**
     * Creates a Health item with standard product fields.
     */
    public Health(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
