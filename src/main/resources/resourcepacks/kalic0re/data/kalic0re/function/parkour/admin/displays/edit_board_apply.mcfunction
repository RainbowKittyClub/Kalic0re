# The Edit dialog's Apply: the board whose title is at $(x) $(y) $(z), at size $(size), with the texts' title (empty
# is the course's own), position and rotation, the place checked before anything changes.
$execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.leaderboard_display_gone",color:"red"}
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
execute unless function kalic0re:parkour/admin/text/place_readable run return run function kalic0re:parkour/admin/text/refuse_place
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
data modify storage kalic0re:parkour tmp.leaderboardDisplayRetitle set from storage kalic0re:parkour tmp.dialogTexts.title
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},x=$(x),y=$(y),z=$(z),distance=..0.01] run \
    function kalic0re:parkour/admin/displays/leaderboard_retitle
data remove storage kalic0re:parkour tmp.leaderboardDisplayRetitle
# Moved and turned last, as the board is found by where it is.
$data modify storage kalic0re:parkour tmp.dialogTexts merge value {x:$(x),y:$(y),z:$(z)}
function kalic0re:parkour/admin/displays/move_board with storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.dialogTexts
