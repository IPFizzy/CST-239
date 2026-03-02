package model;

import java.math.BigDecimal;

/**
 * Represents a Weapon item in the store.
 * This class exists so the inventory can contain specific game item types.
 *
 * Milestone 3 update:
 * Weapons now implement Comparable so they can be sorted alphabetically by name,
 * ignoring case (example: "axe" and "Axe" are treated the same for ordering).
 */
public class Weapon extends SalableProduct implements Comparable<Weapon> {

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

    /**
     * Compares this weapon to another weapon alphabetically by name, ignoring case.
     *
     * Requirement: comparison should be based on the name of the item and ignore case.
     *
     * @param other the other Weapon to compare against
     * @return negative if this comes before other, 0 if equal, positive if after
     */
    @Override
    public int compareTo(Weapon other) {
        if (other == null) {
            // Non-null objects typically come before null in sorting
            return -1;
        }

        // getName() is never null because SalableProduct validates name in the constructor
        return this.getName().compareToIgnoreCase(other.getName());
    }
}
