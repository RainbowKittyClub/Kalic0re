# Outlines number $(index) of course $(id)'s $(list) if it's in the holder's dimension, or past its last,
# moves the holder's queue on.
$execute unless data storage kalic0re:parkour courses[{id:$(id)}].$(list)[$(index)] run \
    return run function kalic0re:parkour/admin/course_boundary/wand/outline/next_boundary_list
data modify storage kalic0re:parkour tmp.outline.boundaries set value []
$data modify storage kalic0re:parkour tmp.outline.boundaries append from storage kalic0re:parkour courses[{id:$(id)}].$(list)[$(index)]
execute store result score #boundaryInHolderDimension kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/in_dimension with storage kalic0re:parkour tmp.outline.boundaries[0]
execute if score #boundaryInHolderDimension kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/boundaries_next
scoreboard players add @s kalic0re.parkour.boundaryOutlineNextBoundary 1
