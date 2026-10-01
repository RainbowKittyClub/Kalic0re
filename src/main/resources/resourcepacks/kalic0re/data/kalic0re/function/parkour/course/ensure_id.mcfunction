# Gives $(course) a number the first time it needs one, at a start, a finish or in the Leaderboards
# list, for its best-times scoreboard (names can't hold spaces), leaderboard trigger and boundary check.
# lastCourseId is the last one handed out.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].id run return 0
scoreboard players set #id kalic0re.parkour.tmp 0
execute store result score #id kalic0re.parkour.tmp run data get storage kalic0re:parkour lastCourseId
scoreboard players add #id kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour lastCourseId int 1 run scoreboard players get #id kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour courses[{name:"$(course)"}].id int 1 run \
    scoreboard players get #id kalic0re.parkour.tmp
