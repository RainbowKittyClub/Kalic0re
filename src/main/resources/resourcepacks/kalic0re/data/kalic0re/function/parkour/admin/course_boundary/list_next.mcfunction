execute unless data storage kalic0re:parkour tmp.courseBoundaryList.boundaries[0] run return 0
# An nbt component would print the dimension quoted, as SNBT; a macro puts a string in bare.
data modify storage kalic0re:parkour tmp.courseBoundaryList.dim set value ""
data modify storage kalic0re:parkour tmp.courseBoundaryList.dim set from storage kalic0re:parkour tmp.courseBoundaryList.boundaries[0].dim
function kalic0re:parkour/admin/course_boundary/list_line with storage kalic0re:parkour tmp.courseBoundaryList
data remove storage kalic0re:parkour tmp.courseBoundaryList.boundaries[0]
execute store result score #boundary kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.courseBoundaryList.boundary
execute store result storage kalic0re:parkour tmp.courseBoundaryList.boundary int 1 run scoreboard players add #boundary kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/list_next
