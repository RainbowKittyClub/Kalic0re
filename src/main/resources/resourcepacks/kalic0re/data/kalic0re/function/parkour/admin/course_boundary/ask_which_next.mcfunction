execute unless data storage kalic0re:parkour tmp.boundaryHere.found[0] run return 0
# An nbt component would print the dimension quoted, as SNBT; a macro puts a string in bare.
data modify storage kalic0re:parkour tmp.boundaryHere.line set value {dim:""}
data modify storage kalic0re:parkour tmp.boundaryHere.line.key set from storage kalic0re:parkour tmp.boundaryHere.found[0].key
data modify storage kalic0re:parkour tmp.boundaryHere.line.dim set from storage kalic0re:parkour tmp.boundaryHere.found[0].boundary.dim
execute store result storage kalic0re:parkour tmp.boundaryHere.line.index int 1 run \
    scoreboard players get #boundaryAnswer kalic0re.parkour.tmp
function kalic0re:parkour/admin/course_boundary/ask_which_line with storage kalic0re:parkour tmp.boundaryHere.line
data remove storage kalic0re:parkour tmp.boundaryHere.found[0]
scoreboard players add #boundaryAnswer kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/ask_which_next
