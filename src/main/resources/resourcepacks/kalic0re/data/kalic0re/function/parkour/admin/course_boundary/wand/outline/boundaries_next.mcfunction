execute unless data storage kalic0re:parkour tmp.outline.boundaries[0] run return 0
execute store result score #x1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].min[0]
execute store result score #y1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].min[1]
execute store result score #z1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].min[2]
execute store result score #x2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].max[0]
execute store result score #y2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].max[1]
execute store result score #z2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.boundaries[0].max[2]
function kalic0re:parkour/admin/course_boundary/build_boundary
data remove storage kalic0re:parkour tmp.newCourseBoundary
# Starting from the pane and glow of the list being drawn.
data modify storage kalic0re:parkour tmp.outline.box set from storage kalic0re:parkour tmp.outline.next.colors
execute store result storage kalic0re:parkour tmp.outline.box.minX int 1 run scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.minY int 1 run scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.minZ int 1 run scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.topX int 1 run scoreboard players get #maxX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.topY int 1 run scoreboard players get #maxY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.topZ int 1 run scoreboard players get #maxZ kalic0re.parkour.tmp
# Each side less the two 0.15 insets, in tenths: length x 10 - 3.
scoreboard players operation #sidex kalic0re.parkour.tmp = #maxX kalic0re.parkour.tmp
scoreboard players operation #sidex kalic0re.parkour.tmp -= #minX kalic0re.parkour.tmp
scoreboard players operation #sidex kalic0re.parkour.tmp *= #10 kalic0re.parkour.tmp
scoreboard players remove #sidex kalic0re.parkour.tmp 3
scoreboard players operation #sidey kalic0re.parkour.tmp = #maxY kalic0re.parkour.tmp
scoreboard players operation #sidey kalic0re.parkour.tmp -= #minY kalic0re.parkour.tmp
scoreboard players operation #sidey kalic0re.parkour.tmp *= #10 kalic0re.parkour.tmp
scoreboard players remove #sidey kalic0re.parkour.tmp 3
scoreboard players operation #sidez kalic0re.parkour.tmp = #maxZ kalic0re.parkour.tmp
scoreboard players operation #sidez kalic0re.parkour.tmp -= #minZ kalic0re.parkour.tmp
scoreboard players operation #sidez kalic0re.parkour.tmp *= #10 kalic0re.parkour.tmp
scoreboard players remove #sidez kalic0re.parkour.tmp 3
function kalic0re:parkour/admin/course_boundary/wand/outline/segments {axis:"x"}
function kalic0re:parkour/admin/course_boundary/wand/outline/segments {axis:"y"}
function kalic0re:parkour/admin/course_boundary/wand/outline/segments {axis:"z"}
# The inset box's sides, for the faces: width W along x, height H along y, depth D along z.
execute store result storage kalic0re:parkour tmp.outline.box.w double 0.1 run scoreboard players get #sidex kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.h double 0.1 run scoreboard players get #sidey kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.box.d double 0.1 run scoreboard players get #sidez kalic0re.parkour.tmp
function kalic0re:parkour/admin/course_boundary/wand/outline/boundary_box with storage kalic0re:parkour tmp.outline.box
function kalic0re:parkour/admin/course_boundary/wand/outline/boundary_faces with storage kalic0re:parkour tmp.outline.box
data remove storage kalic0re:parkour tmp.outline.boundaries[0]
function kalic0re:parkour/admin/course_boundary/wand/outline/boundaries_next
