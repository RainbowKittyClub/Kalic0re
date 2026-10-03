# As a new line of a board: turned the board's way (summon alone keeps no rotation), lit whatever the hour,
# and marked with its course and line, which leaderboard_refresh fills it by.
tp @s ~ ~ ~ ~ ~
data merge entity @s {billboard:"fixed",line_width:400,shadow:1b,brightness:{sky:15,block:15}, \
    Tags:["kalic0re.parkour.leaderboardDisplay"]}
scoreboard players operation @s kalic0re.parkour.leaderboardDisplayCourseId = #leaderboardDisplayCourseId kalic0re.parkour.tmp
scoreboard players operation @s kalic0re.parkour.leaderboardDisplayLine = #leaderboardDisplayLine kalic0re.parkour.tmp
