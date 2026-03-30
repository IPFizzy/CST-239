package test;

import model.Armor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Armor class.
 */
public class ArmorTest {

    /**
     * Tests that Armor inherits SalableProduct behavior correctly.
     */
    @Test
    public void testArmorCreation() {
        Armor armor = new Armor(
                "Leather Armor",
                "Light armor",
                new BigDecimal("40.00"),
                5
        );

        assertEquals("Leather Armor", armor.getName());
        assertEquals("Light armor", armor.getDescription());
        assertEquals(new BigDecimal("40.00"), armor.getPrice());
        assertEquals(5, armor.getQuantity());
    }
}
