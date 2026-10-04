# For leaderboard_rank_follow, along $(axis): the title's translation less 3 × rank tenths of its up, as the rank's.
$scoreboard players operation #leaderboardDisplayRankAlong kalic0re.parkour.tmp = #rotated$(axis) kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplayRankAlong kalic0re.parkour.tmp *= #leaderboardDisplayRankOffset kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplayRankAlong kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
$scoreboard players operation #leaderboardDisplayRankAt kalic0re.parkour.tmp = #leaderboardDisplayTitle$(axis) kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplayRankAt kalic0re.parkour.tmp -= #leaderboardDisplayRankAlong kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.translation[$(index)] float 0.0001 run \
    scoreboard players get #leaderboardDisplayRankAt kalic0re.parkour.tmp
