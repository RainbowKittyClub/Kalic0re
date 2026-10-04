# The Edit dialog's Apply: the board whose title is at $(x) $(y) $(z), at size $(size), with title $(title), and at
# $(position); an empty title is the course's own.
$execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.leaderboard_display_gone",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_retitle {title:"$(title)"}
# Moved last, as the board is found by where it is. move_board returns 1 once it runs, so 0 is text no position.
scoreboard players set #leaderboardDisplayMoved kalic0re.parkour.tmp 0
$execute store result score #leaderboardDisplayMoved kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/displays/move_board {x:$(x),y:$(y),z:$(z),position:"$(position)"}
$execute if score #leaderboardDisplayMoved kalic0re.parkour.tmp matches 0 run \
    tellraw @s {translate:"kalic0re.parkour.chat.not_a_position",with:["$(position)"],color:"red"}
