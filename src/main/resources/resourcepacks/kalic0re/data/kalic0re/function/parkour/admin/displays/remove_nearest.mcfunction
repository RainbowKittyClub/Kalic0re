# /function kalic0re:parkour/admin/displays/remove_nearest, or the Edit dialog's Remove: the nearest leaderboard
# display whose title is within 8 blocks, title and ranks. The course and its times stay.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/leaderboard_remove_board
