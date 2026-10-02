# As a holder past the last of the list being drawn: after the course boundaries come the exclusion
# boundaries, from the first, and after those the queue ends.
execute if entity @s[tag=kalic0re.parkour.boundaryOutlineDrawingExclusionBoundaries] run \
    return run function kalic0re:parkour/admin/course_boundary/wand/outline/end_boundary_queue
tag @s add kalic0re.parkour.boundaryOutlineDrawingExclusionBoundaries
scoreboard players set @s kalic0re.parkour.boundaryOutlineNextBoundary 0
