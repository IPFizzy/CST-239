package test;

import model.Health;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Health class.
 */
public class HealthTest {

    /**
     * Tests that Health inherits SalableProduct behavior correctly.
     */
    @Test
    public void testHealthCreation() {
        Health health = new Health(
                "Small Health Potion",
                "Restores health",
                new BigDecimal("10.00"),
                12
        );

        assertEquals("Small Health Potion", health.getName());
        assertEquals("Restores health", health.getDescription());
        assertEquals(new BigDecimal("10.00"), health.getPrice());
        assertEquals(12, health.getQuantity());
    }
}
