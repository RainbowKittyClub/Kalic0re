# Starting (or re-starting) a course forgets any checkpoint from the previous one.
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].course set from storage kalic0re:parkour tmp.course
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].checkpoint
# The best shown beside the timer: theirs on this course, or -1 for none yet.
scoreboard players set @s kalic0re.parkour.courseBestTicks -1
$data modify storage kalic0re:parkour tmp.best set value {uuid:$(uuid)}
data modify storage kalic0re:parkour tmp.best.course set from storage kalic0re:parkour tmp.course
function kalic0re:parkour/timer/load_best with storage kalic0re:parkour tmp.best
data remove storage kalic0re:parkour tmp.best
