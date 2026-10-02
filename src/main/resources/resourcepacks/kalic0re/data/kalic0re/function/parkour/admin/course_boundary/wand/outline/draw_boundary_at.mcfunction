# Outlines course $(id)'s boundary number $(index) if it's in the holder's dimension, or past its last,
# ends the holder's queue.
$execute unless data storage kalic0re:parkour courses[{id:$(id)}].courseBoundaries[$(index)] run \
    return run scoreboard players reset @s kalic0re.parkour.boundaryOutlineNextBoundary
data modify storage kalic0re:parkour tmp.outline.boundaries set value []
$data modify storage kalic0re:parkour tmp.outline.boundaries append from storage kalic0re:parkour courses[{id:$(id)}].courseBoundaries[$(index)]
execute store result score #boundaryInHolderDimension kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/in_dimension with storage kalic0re:parkour tmp.outline.boundaries[0]
execute if score #boundaryInHolderDimension kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/boundaries_next
scoreboard players add @s kalic0re.parkour.boundaryOutlineNextBoundary 1
