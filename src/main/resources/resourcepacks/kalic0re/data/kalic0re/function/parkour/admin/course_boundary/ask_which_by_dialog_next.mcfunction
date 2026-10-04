execute unless data storage kalic0re:parkour tmp.boundaryHere.found[0] run return 0
data modify storage kalic0re:parkour tmp.boundaryHere.line.key set from storage kalic0re:parkour tmp.boundaryHere.found[0].key
data modify storage kalic0re:parkour tmp.boundaryHere.line.x1 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.min[0]
data modify storage kalic0re:parkour tmp.boundaryHere.line.y1 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.min[1]
data modify storage kalic0re:parkour tmp.boundaryHere.line.z1 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.min[2]
data modify storage kalic0re:parkour tmp.boundaryHere.line.x2 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.max[0]
data modify storage kalic0re:parkour tmp.boundaryHere.line.y2 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.max[1]
data modify storage kalic0re:parkour tmp.boundaryHere.line.z2 set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.max[2]
function kalic0re:parkour/admin/course_boundary/ask_which_by_dialog_line with storage kalic0re:parkour tmp.boundaryHere.line
data remove storage kalic0re:parkour tmp.boundaryHere.found[0]
execute store result score #boundaryChoice kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.boundaryHere.line.index
execute store result storage kalic0re:parkour tmp.boundaryHere.line.index int 1 run scoreboard players add #boundaryChoice kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/ask_which_by_dialog_next
