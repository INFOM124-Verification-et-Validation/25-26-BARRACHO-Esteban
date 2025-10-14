package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite to confirm that {@link Unit}s correctly (de)occupy squares.
 *
 * @author Jeroen Roosen 
 *
 */
class OccupantTest {

    /**
     * The unit under test.
     */
    private Unit unit;

    /**
     * Resets the unit under test.
     */
    @BeforeEach
    void setUp() {
        unit = new BasicUnit();
    }

    /**
     * Asserts that a unit has no square to start with.
     */
    @Test
    void noStartSquare() {
        assertThat(unit.hasSquare()).isFalse();
        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> unit.getSquare());
    }

    /**
     * Tests that the unit indeed has the target square as its base after
     * occupation.
     */
    @Test
    void testOccupy() {
        Square square = new BasicSquare();
        unit.occupy(square);
        assertThat(unit.hasSquare()).isTrue();
        assertThat(unit.getSquare()).isEqualTo(square);
        assertThat(square.getOccupants()).contains(unit);
    }

    /**
     * Test that the unit indeed has the target square as its base after
     * double occupation.
     */
    @Test
    void testReoccupy() {
        Square first = new BasicSquare();
        Square second = new BasicSquare();
        unit.occupy(first);
        assertThat(first.getOccupants()).contains(unit);
        unit.occupy(second);
        assertThat(unit.getSquare()).isEqualTo(second);
        assertThat(second.getOccupants()).contains(unit);
        assertThat(first.getOccupants()).doesNotContain(unit);
    }
}
