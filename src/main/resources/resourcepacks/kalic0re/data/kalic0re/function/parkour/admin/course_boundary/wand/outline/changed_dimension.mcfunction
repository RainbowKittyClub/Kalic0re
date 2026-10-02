# As any player who just changed dimension, through the advancement of this name, revoked to fire again.
advancement revoke @s only kalic0re:parkour/admin/course_boundary/wand/outline/changed_dimension
execute unless entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run return 0
# A holder's outline was for the dimension left. It goes now: what the queue hadn't cleared by the time
# its chunks unload would be saved with them. The new dimension's is drawn if the wand is still in hand.
function kalic0re:parkour/admin/course_boundary/wand/outline/erase
execute as @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineToClear] \
    if score @s kalic0re.parkour.boundaryOutlineOwner = #owner kalic0re.parkour.tmp run kill @s
execute if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run \
    return run function kalic0re:parkour/admin/course_boundary/wand/outline/show
function kalic0re:parkour/admin/course_boundary/wand/outline/hide
