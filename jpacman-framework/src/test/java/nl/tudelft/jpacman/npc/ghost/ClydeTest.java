package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.parser.MapParser;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ClydeTest {

    private final PacManSprites sprites = new PacManSprites();
    private final GhostFactory ghostFactory = new GhostFactory(sprites);
    private final BoardFactory boardFactory = new BoardFactory(sprites);
    private final LevelFactory levelFactory = new LevelFactory(sprites, ghostFactory);
    private final MapParser parser = new MapParser(levelFactory, boardFactory, ghostFactory);

    private Level map(String... rows) {
        List<String> lines = Arrays.stream(rows).map(r -> r.replace('_', ' ')).toList();
        return parser.parseMap(lines);
    }

    private Player newPlayerFacing(Direction d) {
        PlayerFactory pf = new PlayerFactory(sprites);
        Player p = pf.createPacMan();
        if (d != null) p.setDirection(d);
        return p;
    }

    @Test
    void clydeChasseQuandDistanceAuMoins8() {
        Level level = map(
            "############",
            "#P_______ C#",
            "############"
        );
        Player p = newPlayerFacing(Direction.EAST);
        level.registerPlayer(p);
        Clyde c = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        Optional<Direction> move = c.nextAiMove();
        assertThat(move).isPresent();
        assertThat(move.get()).isEqualTo(Direction.WEST);
    }

    @Test
    void clydeSeDisperseQuandDistanceStrictementMoinsDe8() {
        Level level = map(
            "############",
            "#P__C_____ #",
            "############"
        );
        Player p = newPlayerFacing(Direction.EAST);
        level.registerPlayer(p);
        Clyde c = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        Optional<Direction> move = c.nextAiMove();
        assertThat(move).isPresent();
        assertThat(move.get()).isEqualTo(Direction.EAST);
    }

    @Test
    void clydeBloqueParMursRetourneVide() {
        Level level = map(
            "#########",
            "#P#C   ##",
            "#########"
        );
        Player p = newPlayerFacing(Direction.EAST);
        level.registerPlayer(p);
        Clyde c = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        Optional<Direction> move = c.nextAiMove();
        assertThat(move).isEmpty();
    }

    @Test
    void clydeSansJoueurRetourneVide() {
        Level level = map(
            "#####",
            "# C #",
            "#####"
        );
        Clyde c = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        Optional<Direction> move = c.nextAiMove();
        assertThat(move).isEmpty();
    }
}
