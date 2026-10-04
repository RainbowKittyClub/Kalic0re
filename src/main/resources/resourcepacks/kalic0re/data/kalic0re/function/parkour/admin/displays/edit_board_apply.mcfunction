# The Edit dialog's Apply: the board whose title is at $(x) $(y) $(z), at size $(size) and with title $(title); an
# empty title is the course's own.
$execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.leaderboard_display_gone",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_retitle {title:"$(title)"}
