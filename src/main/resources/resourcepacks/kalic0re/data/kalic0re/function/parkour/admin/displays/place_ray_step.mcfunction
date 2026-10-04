# For place_defaults: steps 0.1 along the look, 64 blocks at most, to the first block the crosshair would stop at,
# as the boundary wand's ray does.
execute unless block ~ ~ ~ #kalic0re:parkour/course_boundary_wand_ray_passes_through run return run \
    function kalic0re:parkour/admin/displays/place_ray_hit
scoreboard players remove #rayStepsLeft kalic0re.parkour.tmp 1
execute if score #rayStepsLeft kalic0re.parkour.tmp matches 1.. positioned ^ ^ ^0.1 run \
    function kalic0re:parkour/admin/displays/place_ray_step
