package nl.tudelft.jpacman.npc.ghost;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.*;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
class InkyTest {

    private PacManSprites sprites;
    private BoardFactory boardFactory;
    private GhostFactory ghostFactory;
    private LevelFactory levelFactory;
    private PlayerFactory playerFactory;

    @BeforeEach
    void setup() {
        sprites = new PacManSprites();
        boardFactory = new BoardFactory(sprites);
        ghostFactory = new GhostFactory(sprites);
        levelFactory = new LevelFactory(sprites, ghostFactory);
        playerFactory = new PlayerFactory(sprites);
    }

    // Utilitaire: parse une carte (murs et espaces) en Level
    private Level parseLevel(List<String> rows) {
        MapParser parser = new MapParser(levelFactory, boardFactory);
        return parser.parseMap(rows);
    }

    // Utilitaire: accès carré
    private Square sq(Level level, int x, int y) {
        return level.getBoard().squareAt(x, y);
    }

    // Utilitaire: crée et place un joueur sur (x,y) avec direction
    private Player placePlayer(Level level, int x, int y, Direction dir) {
        Player p = playerFactory.createPacMan();
        p.occupy(sq(level, x, y));
        p.setDirection(dir);
        return p;
    }

    // Utilitaire: crée et place Inky sur (x,y)
    private Inky placeInky(Level level, int x, int y) {
        Inky inky = (Inky) ghostFactory.createInky();
        inky.occupy(sq(level, x, y));
        return inky;
    }

    // Utilitaire: crée et place Blinky sur (x,y)
    private Blinky placeBlinky(Level level, int x, int y) {
        Blinky blinky = (Blinky) ghostFactory.createBlinky();
        
        blinky.occupy(sq(level, x, y));
        return blinky;
    }

    /**
     * Beau temps #1:
     * - Couloir horizontal.
     * - Player à (2,1) orienté EST -> point B = (4,1).
     * - Blinky à (1,1).
     * - Inky à (9,1).
     * Chemin Blinky->B: E,E,E; destination finale depuis B = (7,1).
     * Inky doit aller vers l'OUEST.
     */
    @Test
    void inkyMovesWestTowardsComputedTargetInHorizontalCorridor() {
        Level level = parseLevel(List.of(
            "############",
            "#          #",
            "############"
        ));

        placePlayer(level, 2, 1, Direction.EAST);
        placeBlinky(level, 1, 1);
        Inky inky = placeInky(level, 9, 1);

        Optional<Direction> dir = inky.nextAiMove();
        assertThat(dir).contains(Direction.WEST);
    }

    /**
     * Beau temps #2:
     * - Petite zone verticale.
     * - Player à (5,5) orienté NORD -> point B = (5,3).
     * - Blinky à (5,4) (un cran sous B).
     * - Inky à (5,6).
     * Chemin Blinky->B: N; destination finale = (5,2).
     * Inky doit aller vers le NORD.
     */
    @Test
    void inkyMovesNorthTowardsComputedTargetInVerticalArea() {
        Level level = parseLevel(List.of(
            "###############",
            "#             #",
            "#             #",
            "#             #",
            "#             #",
            "#             #",
            "#             #",
            "###############"
        ));

        placePlayer(level, 5, 5, Direction.NORTH);
        placeBlinky(level, 5, 4);
        Inky inky = placeInky(level, 5, 6);

        Optional<Direction> dir = inky.nextAiMove();
        assertThat(dir).contains(Direction.NORTH);
    }

    /**
     * Mauvais temps #1: pas de Blinky présent -> nextAiMove() vide.
     */
    @Test
    void emptyWhenNoBlinkyPresent() {
        Level level = parseLevel(List.of(
            "#########",
            "#       #",
            "#       #",
            "#       #",
            "#########"
        ));

        placePlayer(level, 2, 2, Direction.EAST);
        Inky inky = placeInky(level, 6, 2);

        Optional<Direction> dir = inky.nextAiMove();
        assertThat(dir).isEmpty();
    }

    /**
     * Mauvais temps #2: pas de Player présent -> nextAiMove() vide.
     */
    @Test
    void emptyWhenNoPlayerPresent() {
        Level level = parseLevel(List.of(
            "#########",
            "#       #",
            "#       #",
            "#       #",
            "#########"
        ));

        placeBlinky(level, 2, 2);
        Inky inky = placeInky(level, 6, 2);

        Optional<Direction> dir = inky.nextAiMove();
        assertThat(dir).isEmpty();
    }

    /**
     * Mauvais temps #3:
     * - Inky est enfermé par des murs et ne peut pas atteindre la destination,
     *   même si Blinky et Player sont présents.
     * - La carte a deux "salles" séparées par un mur vertical sans ouverture.
     * - Inky dans la salle gauche, Player/Blinky dans la salle droite.
     * => Pas de chemin pour Inky -> nextAiMove() vide.
     */
    @Test
    void emptyWhenInkyCannotReachDestinationDueToWalls() {
        Level level = parseLevel(List.of(
            "#########",
            "#   #   #",
            "#   #   #",
            "#   #   #",
            "#########"
        ));

        // Salle gauche: colonnes 1..3; Salle droite: colonnes 5..7; colonne 4 est un mur plein.
        placePlayer(level, 6, 2, Direction.SOUTH);
        placeBlinky(level, 6, 1);
        Inky inky = placeInky(level, 2, 2);

        Optional<Direction> dir = inky.nextAiMove();
        assertThat(dir).isEmpty();
    }
}
