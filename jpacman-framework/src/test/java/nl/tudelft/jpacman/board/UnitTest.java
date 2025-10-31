package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitTest {

    private BoardFactory factory;

    @BeforeEach
    void setUp() {
        PacManSprites sprites = mock(PacManSprites.class);
        factory = new BoardFactory(sprites);
    }

    /**
     * squaresAheadOf(0) should return the current square regardless of direction.
     */
    @Test
    void squaresAheadOfZeroReturnsCurrentSquare() {
        Square s = new BasicSquare();
        // Create a 1x1 board (world is round: the only cell points to itself in all directions).
        factory.createBoard(new Square[][]{{s}});

        Unit u = new BasicUnit();
        u.occupy(s);
        u.setDirection(Direction.WEST); // any direction

        Square result = u.squaresAheadOf(0);
        assertThat(result).isSameAs(s);
    }

    /**
     * Moving EAST across a horizontal line wraps around the board.
     * With a 3x1 board, 4 steps ahead from index 0 should land on index 1 (since 4 % 3 = 1).
     */
    @Test
    void squaresAheadOfWrapsHorizontallyWhenMovingEast() {
        Square s0 = new BasicSquare();
        Square s1 = new BasicSquare();
        Square s2 = new BasicSquare();
        Board board = factory.createBoard(new Square[][]{{s0}, {s1}, {s2}});
        assertThat(board.getWidth()).isEqualTo(3);
        assertThat(board.getHeight()).isEqualTo(1);

        Unit u = new BasicUnit();
        u.occupy(s0);
        u.setDirection(Direction.EAST);

        Square result = u.squaresAheadOf(4); // 4 steps from s0 -> s1
        assertThat(result).isSameAs(s1);
    }

    /**
     * Moving NORTH on a vertical column wraps around the board.
     * On a 1x3 board, starting at middle (y=1), 2 steps NORTH ends at y=2.
     */
    @Test
    void squaresAheadOfWrapsVerticallyWhenMovingNorth() {
        Square y0 = new BasicSquare();
        Square y1 = new BasicSquare();
        Square y2 = new BasicSquare();
        Board board = factory.createBoard(new Square[][]{{y0, y1, y2}});
        assertThat(board.getWidth()).isEqualTo(1);
        assertThat(board.getHeight()).isEqualTo(3);

        Unit u = new BasicUnit();
        u.occupy(y1); // start at middle (x=0, y=1)
        u.setDirection(Direction.NORTH);

        Square result = u.squaresAheadOf(2); // y=1 -> y=0 -> y=2 (wrap)
        assertThat(result).isSameAs(y2);
    }
}
