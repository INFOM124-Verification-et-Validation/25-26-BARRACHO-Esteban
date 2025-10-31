package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.board.BoardFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ClydeTest {

    private PacManSprites sprites;
    private BoardFactory boardFactory;
    private GhostFactory ghostFactory;
    private LevelFactory levelFactory;

    @BeforeEach
    void setup() {
        sprites = new PacManSprites();
        boardFactory = new BoardFactory(sprites);
        ghostFactory = new GhostFactory(sprites);
        levelFactory = new LevelFactory(sprites, ghostFactory);
    }

    // Utilitaire: parse une carte via GhostMapParser
    private Level parseLevel(List<String> mapRows) {
        GhostMapParser parser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
        return parser.parseMap(mapRows);
    }

    // Utilitaire: enregistre un joueur et le place sur la position 'P' du niveau
    private Player registerPlayer(Level level, Direction initialDirection) {
        PlayerFactory pf = new PlayerFactory(sprites);
        Player player = pf.createPacMan();
        level.registerPlayer(player);
        player.setDirection(initialDirection);
        return player;
    }

    // Utilitaire: retrouve Clyde en scannant le board
    private Clyde findClyde(Board board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Square s = board.squareAt(x, y);
                for (var u : s.getOccupants()) {
                    if (u instanceof Clyde) {
                        return (Clyde) u;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Cas 1 (loin > 8 cases): Clyde avance vers Pac-Man (premier pas de la plus courte trajectoire).
     * Carte: murs autour, P et C sur la même ligne, séparés de 9 cases.
     */
    @Test
    void clydeMovesTowardsPacmanWhenFartherThanEight() {
        Level level = parseLevel(List.of(
            "############",
            "#P        C#",
            "############"
        ));
        registerPlayer(level, Direction.EAST);

        Board board = level.getBoard();
        Clyde clyde = findClyde(board);
        assertThat(clyde).isNotNull();

        Optional<Direction> dir = clyde.nextAiMove();
        assertThat(dir).contains(Direction.WEST); // vers Pac-Man
    }

    /**
     * Cas 2 (proche <= 8 cases): Clyde s’éloigne de Pac-Man (opposé du premier pas de la plus courte trajectoire).
     * Carte: P et C sur la même ligne, séparés de 7 cases.
     */
    @Test
    void clydeMovesAwayFromPacmanWhenWithinEight() {
        Level level = parseLevel(List.of(
            "############",
            "#P     C   #",
            "############"
        ));
        registerPlayer(level, Direction.EAST);

        Board board = level.getBoard();
        Clyde clyde = findClyde(board);
        assertThat(clyde).isNotNull();

        Optional<Direction> dir = clyde.nextAiMove();
        assertThat(dir).contains(Direction.EAST); // s’éloigner (opposé de WEST)
    }

    /**
     * Cas 3 (aucun joueur sur la carte): pas de déplacement possible (empty).
     */
    @Test
    void noMoveWhenNoPlayerPresent() {
        Level level = parseLevel(List.of(
            "#####",
            "#  C#",
            "#####"
        ));

        Board board = level.getBoard();
        Clyde clyde = findClyde(board);
        assertThat(clyde).isNotNull();

        Optional<Direction> dir = clyde.nextAiMove();
        assertThat(dir).isEmpty();
    }

    /**
     * Cas 4 (aucun chemin entre Clyde et Pac-Man): pas de déplacement possible (empty).
     */
    @Test
    void noMoveWhenNoPathExists() {
        Level level = parseLevel(List.of(
            "#####",
            "#P#C#",
            "#####"
        ));
        registerPlayer(level, Direction.EAST);

        Board board = level.getBoard();
        Clyde clyde = findClyde(board);
        assertThat(clyde).isNotNull();

        Optional<Direction> dir = clyde.nextAiMove();
        assertThat(dir).isEmpty();
    }
}
