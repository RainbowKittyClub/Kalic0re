# Adds a time_button.json to the Clear Leaderboard dialog for each entry left in tmp.entries.
execute unless data storage kalic0re:parkour tmp.entries[0] run return 0
$data modify storage kalic0re:dialog args set value {course:"$(course)"}
data modify storage kalic0re:dialog args.player set from storage kalic0re:parkour tmp.entries[0].player
execute store result score #ticks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.entries[0].ticks
function kalic0re:parkour/timer/split_into_args
function kalic0re:parkour/admin/dialog/time_button
data modify storage kalic0re:dialog built.times.actions append from storage kalic0re:dialog built.time_button
data remove storage kalic0re:parkour tmp.entries[0]
$function kalic0re:parkour/admin/dialog/add_time_buttons {course:"$(course)"}
