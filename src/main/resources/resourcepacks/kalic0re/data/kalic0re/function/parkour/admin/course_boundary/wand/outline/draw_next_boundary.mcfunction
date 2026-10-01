execute unless score @s kalic0re.parkour.boundaryOutlineNextBoundary matches 0.. run return 0
data modify storage kalic0re:parkour tmp.outline set value {}
execute store result storage kalic0re:parkour tmp.outline.next.id int 1 run \
    scoreboard players get @s kalic0re.parkour.boundaryOutlineCourseId
execute store result storage kalic0re:parkour tmp.outline.next.index int 1 run \
    scoreboard players get @s kalic0re.parkour.boundaryOutlineNextBoundary
function kalic0re:parkour/admin/course_boundary/wand/outline/draw_boundary_at with storage kalic0re:parkour tmp.outline.next
data remove storage kalic0re:parkour tmp.outline
