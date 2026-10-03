# /function kalic0re:parkour/admin/displays/edit_nearest_apply {size:<above 0>,title:"<text, or empty>"}, or the
# Edit dialog's Apply: the nearest leaderboard display whose title is within 8 blocks, at that size and with
# that title; an empty title is the course's own.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_retitle {title:"$(title)"}
