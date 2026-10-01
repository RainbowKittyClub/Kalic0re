# Steps 0.1 along the look, 64 blocks at most, to the first block the crosshair would stop at.
execute unless block ~ ~ ~ #kalic0re:parkour/course_boundary_wand_ray_passes_through align xyz run return run \
    function kalic0re:parkour/admin/course_boundary/wand/mark_here
scoreboard players remove #rayStepsLeft kalic0re.parkour.tmp 1
execute if score #rayStepsLeft kalic0re.parkour.tmp matches 1.. positioned ^ ^ ^0.1 run \
    function kalic0re:parkour/admin/course_boundary/wand/ray_step
