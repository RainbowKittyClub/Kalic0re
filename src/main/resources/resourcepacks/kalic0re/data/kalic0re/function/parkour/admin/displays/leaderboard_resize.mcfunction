# As a board's title: it and its ranks at size $(size), the ranks 0.3 times that apart, as at size 1.
$data modify storage kalic0re:parkour tmp.leaderboardDisplayScale set value [$(size)f,$(size)f,$(size)f]
data modify entity @s transformation.scale set from storage kalic0re:parkour tmp.leaderboardDisplayScale
execute store result score #leaderboardDisplayScaleY kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayScale[1] 1000
execute on passengers run function kalic0re:parkour/admin/displays/leaderboard_scale_rank
data remove storage kalic0re:parkour tmp.leaderboardDisplayScale
