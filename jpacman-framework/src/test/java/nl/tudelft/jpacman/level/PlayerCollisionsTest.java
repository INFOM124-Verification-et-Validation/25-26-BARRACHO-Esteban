package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PlayerCollisionsTest {

    private PacManSprites sprites;
    private PlayerFactory playerFactory;
    private GhostFactory ghostFactory;

    private PlayerCollisions collisions;

    /**
     * Carré de test minimal pour y placer des pellets.
     */
    private static class TestSquare extends Square {
        @Override
        public boolean isAccessibleTo(nl.tudelft.jpacman.board.Unit unit) {
            return true;
        }

        @Override
        @SuppressWarnings("return.type.incompatible")
        public Sprite getSprite() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        sprites = new PacManSprites();
        playerFactory = new PlayerFactory(sprites);
        ghostFactory = new GhostFactory(sprites);

        collisions = new PlayerCollisions();
    }

    /**
     * Player -> Ghost : le joueur meurt.
     */
    @Test
    void playerCollidesGhost_PlayerDies() {
        Player player = playerFactory.createPacMan();
        Ghost ghost = ghostFactory.createBlinky();

        assertThat(player.isAlive()).isTrue();

        collisions.collide(player, ghost);

        assertThat(player.isAlive()).isFalse();
    }

    /**
     * Ghost -> Player : le joueur meurt (symétrique).
     */
    @Test
    void ghostCollidesPlayer_PlayerDies() {
        Player player = playerFactory.createPacMan();
        Ghost ghost = ghostFactory.createBlinky();

        assertThat(player.isAlive()).isTrue();

        collisions.collide(ghost, player);

        assertThat(player.isAlive()).isFalse();
    }

    /**
     * Player -> Pellet : le score augmente et le pellet disparaît de sa case.
     */
    @Test
    void playerCollidesPellet_ScoreIncreasesAndPelletRemoved() {
        Player player = playerFactory.createPacMan();

        Sprite pelletSprite = mock(Sprite.class);
        Pellet pellet = new Pellet(10, pelletSprite);

        Square square = new TestSquare();
        pellet.occupy(square);
        assertThat(pellet.hasSquare()).isTrue();
        int initialScore = player.getScore();

        collisions.collide(player, pellet);

        assertThat(player.getScore()).isEqualTo(initialScore + 10);
        assertThat(pellet.hasSquare()).isFalse();
        assertThat(square.getOccupants()).doesNotContain(pellet);
    }

    /**
     * Pellet -> Player : même effet (symétrique) : score + pellet retiré.
     */
    @Test
    void pelletCollidesPlayer_ScoreIncreasesAndPelletRemoved() {
        Player player = playerFactory.createPacMan();

        Sprite pelletSprite = mock(Sprite.class);
        Pellet pellet = new Pellet(5, pelletSprite);

        Square square = new TestSquare();
        pellet.occupy(square);
        int initialScore = player.getScore();

        collisions.collide(pellet, player);

        assertThat(player.getScore()).isEqualTo(initialScore + 5);
        assertThat(pellet.hasSquare()).isFalse();
        assertThat(square.getOccupants()).doesNotContain(pellet);
    }

    /**
     * Ghost -> Pellet : rien ne se passe.
     */
    @Test
    void ghostCollidesPellet_NothingHappens() {
        Ghost ghost = ghostFactory.createBlinky();

        Sprite pelletSprite = mock(Sprite.class);
        Pellet pellet = new Pellet(7, pelletSprite);

        Square square = new TestSquare();
        pellet.occupy(square);

        collisions.collide(ghost, pellet);

        assertThat(pellet.hasSquare()).isTrue();
        assertThat(square.getOccupants()).contains(pellet);
    }

    /**
     * Pellet -> Ghost : rien ne se passe (symétrique).
     */
    @Test
    void pelletCollidesGhost_NothingHappens() {
        Ghost ghost = ghostFactory.createBlinky();

        Sprite pelletSprite = mock(Sprite.class);
        Pellet pellet = new Pellet(3, pelletSprite);

        Square square = new TestSquare();
        pellet.occupy(square);

        collisions.collide(pellet, ghost);

        assertThat(pellet.hasSquare()).isTrue();
        assertThat(square.getOccupants()).contains(pellet);
    }

    /**
     * Ghost -> Ghost : rien ne se passe.
     */
    @Test
    void ghostCollidesGhost_NothingHappens() {
        Ghost g1 = ghostFactory.createBlinky();
        Ghost g2 = ghostFactory.createBlinky();

        // On vérifie seulement l’absence d’exception / d’effet observable
        collisions.collide(g1, g2);
    }

    /**
     * Player -> Player : rien ne se passe.
     */
    @Test
    void playerCollidesPlayer_NothingHappens() {
        Player p1 = playerFactory.createPacMan();
        Player p2 = playerFactory.createPacMan();

        int score1 = p1.getScore();
        int score2 = p2.getScore();

        collisions.collide(p1, p2);

        assertThat(p1.isAlive()).isTrue();
        assertThat(p2.isAlive()).isTrue();
        assertThat(p1.getScore()).isEqualTo(score1);
        assertThat(p2.getScore()).isEqualTo(score2);
    }

    /**
     * Pellet -> Pellet : rien ne se passe.
     */
    @Test
    void pelletCollidesPellet_NothingHappens() {
        Sprite s = mock(Sprite.class);
        Pellet a = new Pellet(1, s);
        Pellet b = new Pellet(2, s);

        Square square = new TestSquare();
        a.occupy(square);
        b.occupy(square);

        collisions.collide(a, b);

        assertThat(a.hasSquare()).isTrue();
        assertThat(b.hasSquare()).isTrue();
        assertThat(square.getOccupants()).contains(a, b);
    }
}
