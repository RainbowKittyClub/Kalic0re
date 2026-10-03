$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return 0
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.record.id set from storage kalic0re:parkour courses[{name:"$(course)"}].id
# Added on every record, failing harmlessly once it exists, so the scoreboard is back if removed.
function kalic0re:parkour/course/add_best_objective with storage kalic0re:parkour tmp.record
# Each finish saves the player's entry again: their best, now the faster of it and this run, under
# their current name.
execute store result score #ticks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.record.ticks
scoreboard players operation #best kalic0re.parkour.tmp = #ticks kalic0re.parkour.tmp
scoreboard players set #newBest kalic0re.parkour.tmp 1
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}] run \
    function kalic0re:parkour/timer/take_best {course:"$(course)",uuid:$(uuid)}
execute store result storage kalic0re:parkour tmp.record.best int 1 run scoreboard players get #best kalic0re.parkour.tmp
# Its place in the list, kept fastest first: after every time no slower than it.
data modify storage kalic0re:parkour tmp.scan set value []
$data modify storage kalic0re:parkour tmp.scan set from storage kalic0re:parkour courses[{name:"$(course)"}].times
scoreboard players operation #ticks kalic0re.parkour.tmp = #best kalic0re.parkour.tmp
scoreboard players set #index kalic0re.parkour.tmp 0
function kalic0re:parkour/timer/count_no_slower
execute store result storage kalic0re:parkour tmp.record.index int 1 run scoreboard players get #index kalic0re.parkour.tmp
data remove storage kalic0re:parkour tmp.scan
function kalic0re:parkour/timer/store with storage kalic0re:parkour tmp.record
function kalic0re:parkour/admin/displays/leaderboard_refresh with storage kalic0re:parkour tmp.record
scoreboard players operation @s kalic0re.parkour.courseBestTicks = #best kalic0re.parkour.tmp
function kalic0re:parkour/timer/announce with storage kalic0re:parkour tmp.record
