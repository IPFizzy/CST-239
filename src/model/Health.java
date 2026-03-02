package model;

import java.math.BigDecimal;

/**
 * Represents a Health item (like potions) in the store.
 * This class exists so the inventory can contain specific game item types.
 */
public class Health extends SalableProduct {

    /**
     * Creates a Health item with the same core fields as a SalableProduct.
     *
     * @param name        item name
     * @param description item description
     * @param price       item price
     * @param quantity    item quantity in stock
     */
    public Health(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
