# As a runner who just finished, at them: their time goes on the course's scoreboard and leaderboard.
execute unless score @s kalic0re.parkour.runTicks matches 0.. run return 0
$data modify storage kalic0re:parkour tmp.record set value {uuid:$(uuid)}
$data modify storage kalic0re:parkour tmp.record.course set from storage kalic0re:parkour players[{uuid:$(uuid)}].course
execute store result storage kalic0re:parkour tmp.record.ticks int 1 run scoreboard players get @s kalic0re.parkour.runTicks
function kalic0re:parkour/timer/probe_name
function kalic0re:parkour/timer/record_on_course with storage kalic0re:parkour tmp.record
data remove storage kalic0re:parkour tmp.record
