# As a new rank line, at its title: drawn 0.3 per rank below it along the board's own down, as a line is 0.25
# tall with its background. Turned before it mounts, since a tp dismounts a rider.
function kalic0re:parkour/admin/displays/leaderboard_line_setup
execute store result entity @s transformation.translation[1] float -0.3 run \
    scoreboard players get #leaderboardDisplayLine kalic0re.parkour.tmp
ride @s mount @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplayNewTitle,distance=..0.01]
