# As a rank riding its title, for leaderboard_resize: the scale in tmp.leaderboardDisplayScale, and 0.3 per
# rank below the title times its height, #leaderboardDisplayScaleY in thousandths.
data modify entity @s transformation.scale set from storage kalic0re:parkour tmp.leaderboardDisplayScale
scoreboard players operation #leaderboardDisplayRankOffset kalic0re.parkour.tmp = @s kalic0re.parkour.leaderboardDisplayLine
scoreboard players operation #leaderboardDisplayRankOffset kalic0re.parkour.tmp *= #leaderboardDisplayScaleY kalic0re.parkour.tmp
execute store result entity @s transformation.translation[1] float -0.0003 run \
    scoreboard players get #leaderboardDisplayRankOffset kalic0re.parkour.tmp
