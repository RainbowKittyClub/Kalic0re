# The Place Leaderboard Display dialog's button: a board of size $(size) for $(course), its title at $(position) and
# facing $(rotation), as positioned and rotated read them, so ~ counts from where you are and the way you face.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
scoreboard players set #leaderboardDisplayPlaceRead kalic0re.parkour.tmp 0
$execute store result score #leaderboardDisplayPlaceRead kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course/read_place {position:"$(position)",rotation:"$(rotation)"}
$execute if score #leaderboardDisplayPlaceRead kalic0re.parkour.tmp matches 0 run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.not_a_place",with:["$(position)","$(rotation)"],color:"red"}
$function kalic0re:parkour/admin/displays/leaderboard_sized {course:"$(course)",position:"$(position)",rotation:"$(rotation)",size:$(size)}
