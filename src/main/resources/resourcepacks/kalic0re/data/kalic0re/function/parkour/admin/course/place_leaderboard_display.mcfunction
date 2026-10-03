# The Place Leaderboard Display dialog's button: a board of size $(size) $(distance) ahead of you, its title
# $(height) above your feet, facing you; with square 1, straight north, east, south or west.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.leaderboardDisplayPlacement set value \
    {course:"$(course)",distance:$(distance),height:$(height),size:$(size)}
data modify storage kalic0re:parkour tmp.leaderboardDisplayPlacement.yaw set from entity @s Rotation[0]
# A score, not an NBT match: a macro writes 1b as plain 1.
$scoreboard players set #leaderboardDisplaySquare kalic0re.parkour.tmp $(square)
execute if score #leaderboardDisplaySquare kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/displays/leaderboard_square_yaw
function kalic0re:parkour/admin/displays/leaderboard_ahead with storage kalic0re:parkour tmp.leaderboardDisplayPlacement
data remove storage kalic0re:parkour tmp.leaderboardDisplayPlacement
