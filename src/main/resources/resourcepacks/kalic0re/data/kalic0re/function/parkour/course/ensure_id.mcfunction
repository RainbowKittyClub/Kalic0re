# Gives $(course) a number the first time a run on it finishes, for its best-times scoreboard,
# whose name can't hold spaces, and its leaderboard link. lastCourseId is the last one handed out.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].id run return 0
scoreboard players set #id kalic0re.parkour.tmp 0
execute store result score #id kalic0re.parkour.tmp run data get storage kalic0re:parkour lastCourseId
scoreboard players add #id kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour lastCourseId int 1 run scoreboard players get #id kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour courses[{name:"$(course)"}].id int 1 run \
    scoreboard players get #id kalic0re.parkour.tmp
