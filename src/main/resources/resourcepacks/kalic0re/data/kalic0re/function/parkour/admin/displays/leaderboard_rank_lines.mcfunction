# Ranks #leaderboardDisplayRank to 10, from the first of tmp.leaderboardDisplay.times on: the time as the
# leaderboard dialog shows it, or the rank and no time while the list is shorter.
execute if score #leaderboardDisplayRank kalic0re.parkour.tmp matches 11.. run return 0
execute store result storage kalic0re:parkour tmp.leaderboardDisplay.rank int 1 run \
    scoreboard players get #leaderboardDisplayRank kalic0re.parkour.tmp
execute if data storage kalic0re:parkour tmp.leaderboardDisplay.times[0] run function kalic0re:parkour/admin/displays/leaderboard_time_line
execute unless data storage kalic0re:parkour tmp.leaderboardDisplay.times[0] run \
    function kalic0re:parkour/admin/displays/leaderboard_empty_line with storage kalic0re:parkour tmp.leaderboardDisplay
data remove storage kalic0re:parkour tmp.leaderboardDisplay.times[0]
scoreboard players add #leaderboardDisplayRank kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/displays/leaderboard_rank_lines
