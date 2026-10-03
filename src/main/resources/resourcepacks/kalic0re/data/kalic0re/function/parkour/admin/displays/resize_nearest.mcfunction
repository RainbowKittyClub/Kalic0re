# /function kalic0re:parkour/admin/displays/resize_nearest {size:<above 0>}: the nearest leaderboard display
# whose title is within 8 blocks, at that size; 1 is as placed. The Edit dialog's Apply is edit_nearest_apply.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
$execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_resize {size:$(size)}
