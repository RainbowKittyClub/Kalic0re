# For edit_board_apply: the board whose title is at $(x) $(y) $(z) moved to $(position) and turned to $(rotation),
# as positioned and rotated read them, so ~ counts from where you are and the way you face, and a whole-number x
# or z is the block's centre; its ranks ride the title, so they come and turn too. Alone in a function of its own:
# text that isn't a place fails only this, which then returns nothing.
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] \
    positioned $(position) rotated $(rotation) run tp @s ~ ~ ~ ~ ~
return 1
