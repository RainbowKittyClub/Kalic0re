# For leaderboard_ranks_follow_title: #rotated$(axis) times the title's scale along it.
$execute store result score #leaderboardDisplayFollowScale kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.scale[$(index)] 10000
$scoreboard players operation #rotated$(axis) kalic0re.parkour.tmp *= #leaderboardDisplayFollowScale kalic0re.parkour.tmp
$scoreboard players operation #rotated$(axis) kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
