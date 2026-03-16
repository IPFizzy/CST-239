package model;

import java.math.BigDecimal;

/**
 * Represents a Weapon item in the store.
 * This class exists so the inventory can contain specific game item types.
 *
 * Milestone 5 update:
 * Weapons no longer need their own Comparable implementation because
 * SalableProduct now provides the natural alphabetical ordering for all products.
 */
public class Weapon extends SalableProduct {

    /**
     * Creates a Weapon with the same core fields as a SalableProduct.
     *
     * @param name        weapon name
     * @param description weapon description
     * @param price       weapon price
     * @param quantity    weapon quantity in stock
     */
    public Weapon(String name, String description, BigDecimal price, int quantity) {
        super(name, description, price, quantity);
    }
}
