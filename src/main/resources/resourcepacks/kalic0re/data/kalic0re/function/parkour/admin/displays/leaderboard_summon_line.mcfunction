# Line #leaderboardDisplayLine here, then the next 0.3 below along the board's own down: a line is 0.25 tall
# with its background, so 0.05 between them. 0 is the title, 1 to 10 the ranks.
execute summon minecraft:text_display run function kalic0re:parkour/admin/displays/leaderboard_line_setup
scoreboard players add #leaderboardDisplayLine kalic0re.parkour.tmp 1
execute if score #leaderboardDisplayLine kalic0re.parkour.tmp matches ..10 positioned ^ ^-0.3 ^ run \
    function kalic0re:parkour/admin/displays/leaderboard_summon_line
