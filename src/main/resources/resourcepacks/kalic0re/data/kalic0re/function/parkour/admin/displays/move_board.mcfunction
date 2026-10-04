# For edit_board_apply: the board whose title is at $(x) $(y) $(z) moved to $(position) as positioned reads it, so
# ~ counts from you and a whole-number x or z is the block's centre; its ranks ride the title, so they come too.
# Alone in a function of its own: text that isn't a position fails only this, which then returns nothing.
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] \
    positioned $(position) run tp @s ~ ~ ~
return 1
