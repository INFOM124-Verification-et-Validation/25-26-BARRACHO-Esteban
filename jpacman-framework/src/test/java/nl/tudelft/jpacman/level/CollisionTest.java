package nl.tudelft.jpacman.level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CollisionTest {

    private PlayerCollisions collisions;
    private Score pointCalculator;


    @BeforeEach
    void setup() {
        pointCalculator = mock(Score.class);
    }

    // --- T1 : Player -> Pellet
    @Test
    void playerEatsPellet() {
        collisions.collide(any(), any());
        when(pointCalculator.getPoints()).thenReturn(10);
    }

    // --- T2 : Player -> Ghost

    // --- T3 : Ghost -> Player (symétrie)

    // --- T4 : Ghost -> Pellet (no-op)

    // --- T5 : Player -> Player (no-op)

    // --- T6 : Ghost -> Ghost (no-op)
}
