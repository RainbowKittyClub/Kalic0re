# For edit_board_apply: the board whose title is at $(x) $(y) $(z) moved to $(position) and turned to $(rotation);
# its ranks ride the title, so they come and turn too.
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] \
    positioned $(position) rotated $(rotation) run tp @s ~ ~ ~ ~ ~
