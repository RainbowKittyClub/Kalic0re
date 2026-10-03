# /function kalic0re:parkour/admin/displays/leaderboard {course:"<name>",position:"<x y z>",rotation:"<yaw pitch>"}
# e.g. {course:"Church of the Verdant Light",position:"~ ~2 ~",rotation:"~180 0"}
# A board of the course's ten fastest times: its title at position, then a line per rank below, each a text
# display of its own, fixed, facing the way rotation points (~180 0 faces you). Kept up to date as times change.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$execute store result score #leaderboardDisplayCourseId kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].id
scoreboard players set #leaderboardDisplayLine kalic0re.parkour.tmp 0
$execute positioned $(position) rotated $(rotation) run function kalic0re:parkour/admin/displays/leaderboard_summon_line
$function kalic0re:parkour/admin/displays/leaderboard_refresh_named {course:"$(course)"}
