# As a new board's title, at its position and turned its way: the ranks are summoned here and ride it, then
# all take the size in tmp.leaderboardDisplayNew, unless it's 1, as summoned.
function kalic0re:parkour/admin/displays/leaderboard_line_setup
tag @s add kalic0re.parkour.leaderboardDisplayNewTitle
scoreboard players set #leaderboardDisplayLine kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/displays/leaderboard_summon_rank
tag @s remove kalic0re.parkour.leaderboardDisplayNewTitle
execute store result score #leaderboardDisplaySize kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayNew.size 1000
execute unless score #leaderboardDisplaySize kalic0re.parkour.tmp matches 1000 run \
    function kalic0re:parkour/admin/displays/leaderboard_resize with storage kalic0re:parkour tmp.leaderboardDisplayNew
