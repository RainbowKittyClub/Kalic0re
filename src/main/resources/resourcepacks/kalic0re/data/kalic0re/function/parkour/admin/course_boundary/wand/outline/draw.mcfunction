# As the wand's holder, with its data in tmp.wand.held: their selection in yellow now, and their course's
# boundaries in white and exclusion boundaries in orange from the next tick, a few a tick. Owner scores
# find the displays again.
tag @s add kalic0re.parkour.holdingBoundaryWand
scoreboard players reset @s kalic0re.parkour.boundaryWandHandChangeTicks
effect give @s minecraft:night_vision 6 0 true
execute unless score @s kalic0re.parkour.boundaryOutlineOwner matches 1.. run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/assign_owner
function kalic0re:parkour/admin/course_boundary/wand/outline/erase
data modify storage kalic0re:parkour tmp.outline set value {}
function kalic0re:parkour/admin/course_boundary/wand/outline/selection
function kalic0re:parkour/admin/course_boundary/wand/outline/own_new_displays
data remove storage kalic0re:parkour tmp.outline
function kalic0re:parkour/admin/course_boundary/wand/outline/queue_boundaries with storage kalic0re:parkour tmp.wand.held
schedule function kalic0re:parkour/admin/course_boundary/wand/outline/sweep 20t replace
