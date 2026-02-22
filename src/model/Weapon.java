package model;

import java.math.BigDecimal;

/**
 * Represents a Weapon item in the store.
 * This class exists so the inventory can contain specific game item types.
 */
public class Weapon extends SalableProduct {

    /**
     * Creates a Weapon with the same core fields as a SalableProduct.
     */
    public Weapon(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
