Battleships

1 Battleship     5 Blocks

1 Cruiser        4 Blocks

2 Frigates       3 Blocks

1 Minesweeper    2 Blocks




Placement phase:

Place your battleships on the battlefield by entering coordinates when prompted. Coordinates during placement have the following format: [A-J][0-9][V|!V]. The letter defines the position on the x-Axis, while the number defines the position on the y-Axis. V indicates, that the ship should be placed vertically, giving a different character places the ship horizontally.

Guess phase:

Enter coordinates [A-J][0-9] when prompted. After all of the opponents ships are sunk, you win the game. However, if you are player 0 and the opponent manages to destroy your last ship after you destroyed all of his, the game is a tie.


Memory (Enemies preferred shot fields and placements)


// Check for invalid placement options (min shiplength)
// Push coordinates next to hit on stack
        // Enter AttackMode
        // After hitting ship for the second time and !sunk
        // set orientation
        // calculate max enemy shiplength
        // try next field depending on orientation
        // on first miss try other direction if ship has not been sunk
        // fire as long as hit fields < max enemy shiplength || ship sunk
        // Mark everything around sunk ship as IGNORE
        // Remove ignored fields from checkerboard stack

https://de.wikihow.com/Bei-Schiffe-versenken-gewinnen
