# Outlines course $(id)'s boundary number $(index), or past its last, ends the holder's queue.
$execute unless data storage kalic0re:parkour courses[{id:$(id)}].courseBoundaries[$(index)] run \
    return run scoreboard players reset @s kalic0re.parkour.boundaryOutlineNextBoundary
data modify storage kalic0re:parkour tmp.outline.boundaries set value []
$data modify storage kalic0re:parkour tmp.outline.boundaries append from storage kalic0re:parkour courses[{id:$(id)}].courseBoundaries[$(index)]
function kalic0re:parkour/admin/course_boundary/wand/outline/boundaries_next
scoreboard players add @s kalic0re.parkour.boundaryOutlineNextBoundary 1
