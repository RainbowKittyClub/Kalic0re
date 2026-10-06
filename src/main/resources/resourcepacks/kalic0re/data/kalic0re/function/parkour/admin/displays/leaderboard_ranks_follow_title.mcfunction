# As a board's title: its ranks given its facing and transformation, each still 0.3 a rank below it in its own text,
# so a title turned, scaled or moved by hand, as a world editor does, takes them along.
data modify storage kalic0re:parkour tmp.leaderboardDisplayFollow set value {}
data modify storage kalic0re:parkour tmp.leaderboardDisplayFollow.Rotation set from entity @s Rotation
data modify storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation set from entity @s transformation
# Up in its text, in ten-thousandths, as drawn: turned by right_rotation, scaled, then turned by left_rotation.
scoreboard players set #rotatedX kalic0re.parkour.tmp 0
scoreboard players set #rotatedY kalic0re.parkour.tmp 10000
scoreboard players set #rotatedZ kalic0re.parkour.tmp 0
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.right_rotation
function kalic0re:parkour/direction_marker/rotate
function kalic0re:parkour/admin/displays/leaderboard_follow_scale {axis:"X",index:0}
function kalic0re:parkour/admin/displays/leaderboard_follow_scale {axis:"Y",index:1}
function kalic0re:parkour/admin/displays/leaderboard_follow_scale {axis:"Z",index:2}
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.left_rotation
function kalic0re:parkour/direction_marker/rotate
data remove storage kalic0re:parkour tmp.directionMarkerTransformation
execute store result score #leaderboardDisplayTitleX kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.translation[0] 10000
execute store result score #leaderboardDisplayTitleY kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.translation[1] 10000
execute store result score #leaderboardDisplayTitleZ kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation.translation[2] 10000
execute on passengers run function kalic0re:parkour/admin/displays/leaderboard_rank_follow
data remove storage kalic0re:parkour tmp.leaderboardDisplayFollow
