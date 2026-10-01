# As the holder after a corner click, with tmp.wand.held: only their selection changed, so only it's redrawn.
scoreboard players operation #owner kalic0re.parkour.tmp = @s kalic0re.parkour.boundaryOutlineOwner
execute as @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutline, \
    tag=kalic0re.parkour.boundaryOutlineSelection] if score @s kalic0re.parkour.boundaryOutlineOwner = #owner kalic0re.parkour.tmp run \
    tag @s add kalic0re.parkour.boundaryOutlineToClear
function kalic0re:parkour/admin/course_boundary/wand/outline/queue_tagged_to_clear
data modify storage kalic0re:parkour tmp.outline set value {}
function kalic0re:parkour/admin/course_boundary/wand/outline/selection
function kalic0re:parkour/admin/course_boundary/wand/outline/own_new_displays
data remove storage kalic0re:parkour tmp.outline
