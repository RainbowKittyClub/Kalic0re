# Hands everything this player's wand shows to the clear queue: selection, frames, boundaries and faces.
execute unless score @s kalic0re.parkour.boundaryOutlineOwner matches 1.. run return 0
scoreboard players operation #owner kalic0re.parkour.tmp = @s kalic0re.parkour.boundaryOutlineOwner
execute as @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutline] \
    if score @s kalic0re.parkour.boundaryOutlineOwner = #owner kalic0re.parkour.tmp run \
    tag @s add kalic0re.parkour.boundaryOutlineToClear
function kalic0re:parkour/admin/course_boundary/wand/outline/queue_tagged_to_clear
