# Specification-based Testing

## 1. Goal, inputs and outputs
- Goal: prochaine position de clyde
- Input domain: map, joueur et position de clyde
- Output domain: prochaine position de clyde, rien ou une direction

## 2. Explore the program (if needed)

## 3. Identify input and output partitions

### Input partitions

#### Individual inputs
Ensemble de configuration de valeur de paramètre qui fait que le programme va ce comporté d'une certaine manière,
on va tester les différents cas principal intérêt a testé
#### Distance partitions:
- 01: Clyde is within 8 grid spaces of Pacman (distance < 8)
- 02: Clyde is beyond 8 grid spaces of Pacman (distance > 8)
- 03: Clyde is at exactly 8 grid spaces of Pacman (distance = 8))

#### Obstacle direction partitions:
- 01: Path of Clyde is free
- 02: Path of Clyde is blocked
- 03: Clyde has multiple valid moves

#### Combinations of input values
- Distance < 8 & path free
- Distance < 8 & path blocked
- Distance < 8 & multiple moves
 
- Distance > 8 & path free
- Distance > 8 & path blocked
- Distance > 8 & multiple moves

- Distance = 8 & path free
- Distance = 8 & path blocked
- Distance = 8 & multiple moves

### Output partitions
- Empty direction
- Direction towards pacman
- Direction away from pacman
## 4. Identify boundaries
#### Not valid maps:
- Multiple Clyde on the map
- Multiple Pacman on the map
- Clyde is on Pacman

- Pacman is not on the board partition
- Pacman at the edge of the board partition
- Pacman does not have a square partition
- Pacman goes from one edge of the map to the other
- 
## 5. Select test cases