# The Edit dialog's Remove: the board whose title is at $(x) $(y) $(z), title and ranks. The course and its
# times stay.
$execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.leaderboard_display_gone",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_remove_board
