# /function kalic0re:parkour/admin/displays/leaderboard_sized {course:"<name>",position:"<x y z>",rotation:"<yaw pitch>",size:<above 0>}
# A board of the course's ten fastest times: its title at position and a line per rank below, each a text
# display, fixed, facing the way rotation points (~180 0 faces you). Kept up to date as times change.
# The ranks ride the title, so a tp of the title moves and turns the whole board; resize it with
# leaderboard_resize as the title, or admin/displays/resize_nearest. All share one position: pick the title
# by kalic0re.parkour.leaderboardDisplayLine=0.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$execute store result score #leaderboardDisplayCourseId kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].id
scoreboard players set #leaderboardDisplayLine kalic0re.parkour.tmp 0
$data modify storage kalic0re:parkour tmp.leaderboardDisplayNew set value {size:$(size)}
$execute positioned $(position) rotated $(rotation) summon minecraft:text_display run \
    function kalic0re:parkour/admin/displays/leaderboard_title_setup
data remove storage kalic0re:parkour tmp.leaderboardDisplayNew
$function kalic0re:parkour/admin/displays/leaderboard_refresh_named {course:"$(course)"}
