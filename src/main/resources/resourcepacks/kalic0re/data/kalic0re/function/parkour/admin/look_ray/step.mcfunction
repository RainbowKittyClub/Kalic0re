# Steps 0.1 along the look, #rayStepsLeft steps at most, to the first block the crosshair would stop at, and runs
# $(hit) there. Callers anchor it at the eyes, then back at the feet, or each ^ ^ ^ would add the eye height again.
$execute unless block ~ ~ ~ #kalic0re:parkour/look_ray_passes_through run return run function $(hit)
scoreboard players remove #rayStepsLeft kalic0re.parkour.tmp 1
$execute if score #rayStepsLeft kalic0re.parkour.tmp matches 1.. positioned ^ ^ ^0.1 run \
    function kalic0re:parkour/admin/look_ray/step {hit:"$(hit)"}
