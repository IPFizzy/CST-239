package model;

import java.math.BigDecimal;

/**
 * Represents an Armor item in the store.
 * This class exists so the inventory can contain specific game item types.
 */
public class Armor extends SalableProduct {

    /**
     * Creates an Armor with the same core fields as a SalableProduct.
     *
     * @param name        armor name
     * @param description armor description
     * @param price       armor price
     * @param quantity    armor quantity in stock
     */
    public Armor(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
