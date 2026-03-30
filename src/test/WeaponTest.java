package test;

import model.Weapon;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Weapon class.
 */
public class WeaponTest {

    /**
     * Tests that Weapon inherits SalableProduct behavior correctly.
     */
    @Test
    public void testWeaponCreation() {
        Weapon weapon = new Weapon(
                "Hunter Bow",
                "Long range bow",
                new BigDecimal("55.00"),
                4
        );

        assertEquals("Hunter Bow", weapon.getName());
        assertEquals("Long range bow", weapon.getDescription());
        assertEquals(new BigDecimal("55.00"), weapon.getPrice());
        assertEquals(4, weapon.getQuantity());
    }
}
