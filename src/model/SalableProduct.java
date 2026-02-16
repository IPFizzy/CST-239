package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents a product that can be sold in the store.
 *
 * This class models the core data for an item available in inventory.
 * Each product has a name, description, price, and quantity in stock.
 *
 * The name acts as the identifier for equality comparisons in this version
 * of the application.
 */
public class SalableProduct {

    // Name is final because a product's identity should not change once created.
    private final String name;

    // Description can be updated if needed (for example, editing product details).
    private String description;

    // BigDecimal is used instead of double to avoid floating point rounding errors.
    private BigDecimal price;

    // Tracks how many units are currently available in inventory.
    private int quantity;

    /**
     * Creates a new SalableProduct with validated input.
     *
     * Validation is done here to guarantee that no product
     * is ever created in an invalid state.
     *
     * @param name        Product name (cannot be null or blank)
     * @param description Product description (null becomes empty string)
     * @param price       Product price (must be >= 0)
     * @param quantity    Quantity in stock (must be >= 0)
     */
    public SalableProduct(String name, String description, BigDecimal price, int quantity) {

        // Prevent creating a product without a meaningful identifier.
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null/blank.");
        }

        // Use setters so validation logic is centralized in one place.
        setPrice(price);
        setQuantity(quantity);

        this.name = name.trim();
        this.description = (description == null) ? "" : description.trim();
    }

    /**
     * Returns the product name.
     *
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the product description.
     *
     * @return product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the product description.
     * Null values are converted to an empty string to avoid null checks elsewhere.
     *
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    /**
     * Returns the current price of the product.
     *
     * @return price as BigDecimal
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * Price must be non-null and non-negative.
     * The value is normalized to two decimal places to represent currency properly.
     *
     * @param price price value
     */
    public void setPrice(BigDecimal price) {

        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        // Normalize to two decimal places for consistent currency formatting.
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the quantity currently in stock.
     *
     * @return quantity available
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity.
     *
     * Quantity cannot be negative. This ensures inventory integrity.
     *
     * @param quantity number of units in stock
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Decreases the available stock by the specified amount.
     *
     * This method prevents inventory from going below zero.
     *
     * @param amount amount to subtract
     */
    public void decreaseStock(int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Decrease amount must be > 0.");
        }

        if (quantity < amount) {
            throw new IllegalStateException("Not enough stock to decrease by " + amount + ".");
        }

        quantity -= amount;
    }

    /**
     * Increases the available stock.
     * Used when restocking or canceling a purchase.
     *
     * @param amount amount to add
     */
    public void increaseStock(int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Increase amount must be > 0.");
        }

        quantity += amount;
    }

    /**
     * Returns a formatted string representation of the product.
     * This is used when printing inventory in the console application.
     */
    @Override
    public String toString() {
        return String.format("%s ($%s) qty=%d - %s", name, price, quantity, description);
    }

    /**
     * Products are considered equal if their names match (case-insensitive).
     * This supports using the product name as a logical identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SalableProduct)) {
            return false;
        }

        SalableProduct other = (SalableProduct) o;
        return name.equalsIgnoreCase(other.name);
    }

    /**
     * Hash code is derived from the lowercase name to remain consistent with equals().
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
