package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Base class for any product that can be sold in the Store Front.
 *
 * Each product has:
 * - name (read-only once constructed)
 * - description
 * - price
 * - quantity (stock level)
 *
 * This class is designed to be extended by specific item types like Weapon, Armor, and Health.
 *
 * Milestone 5 update:
 * SalableProduct now implements Comparable so all products can be sorted
 * alphabetically by name using Collections Framework utilities.
 */
public class SalableProduct implements Comparable<SalableProduct> {

    // readOnly by design, we only set it during construction
    private final String name;

    private String description;
    private BigDecimal price;
    private int quantity;

    /**
     * Creates a salable product with the required core fields.
     *
     * @param name        Name of the product (required)
     * @param description Description of the product
     * @param price       Price of the product
     * @param quantity    Current stock quantity
     */
    public SalableProduct(String name, String description, BigDecimal price, int quantity) {

        // Basic validation to avoid bad data getting into the system
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        this.name = name.trim();
        this.description = (description == null) ? "" : description.trim();
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * @return product name (read-only)
     */
    public String getName() {
        return name;
    }

    /**
     * @return product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates product description.
     *
     * @param description new description value
     */
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    /**
     * @return product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Updates product price.
     *
     * @param price new price value
     */
    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    /**
     * @return current stock quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Updates product quantity.
     *
     * @param quantity new quantity value
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Decreases stock by a given amount.
     *
     * @param amount amount to decrease by
     */
    public void decreaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Decrease amount cannot be negative.");
        }
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Not enough stock to decrease by that amount.");
        }
        this.quantity -= amount;
    }

    /**
     * Increases stock by a given amount.
     *
     * @param amount amount to increase by
     */
    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increase amount cannot be negative.");
        }
        this.quantity += amount;
    }

    /**
     * Compares products alphabetically by name, ignoring case.
     *
     * This acts as the natural ordering for SalableProduct objects so
     * Collections.sort() can sort by product name.
     *
     * @param other the other product to compare against
     * @return negative if this comes before other, 0 if equal, positive if after
     */
    @Override
    public int compareTo(SalableProduct other) {
        if (other == null) {
            return -1;
        }

        return this.name.compareToIgnoreCase(other.name);
    }

    /**
     * Returns a formatted string representation of the product.
     * This is used when printing inventory in the console application.
     *
     * @return formatted product string for console output
     */
    @Override
    public String toString() {
        return String.format("%-20s  $%-7s  Stock: %-3d  %s",
                name,
                price,
                quantity,
                description);
    }

    /**
     * Products are considered equal if their names match (case-insensitive).
     *
     * @param o other object
     * @return true if same product name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalableProduct)) return false;
        SalableProduct that = (SalableProduct) o;
        return name.equalsIgnoreCase(that.name);
    }

    /**
     * Hash code based on product name (case-insensitive approach by normalizing).
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
