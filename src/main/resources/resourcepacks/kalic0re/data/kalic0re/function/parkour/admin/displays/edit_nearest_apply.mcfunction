# /function kalic0re:parkour/admin/displays/edit_nearest_apply {size:<above 0>,title:"<text, or empty>"}: the nearest
# board within 8 at that size and title, an empty one the course's own. The Edit dialog's Apply is edit_board_apply.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
$data modify storage kalic0re:parkour tmp.leaderboardDisplayRetitle set value "$(title)"
execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_retitle
data remove storage kalic0re:parkour tmp.leaderboardDisplayRetitle
