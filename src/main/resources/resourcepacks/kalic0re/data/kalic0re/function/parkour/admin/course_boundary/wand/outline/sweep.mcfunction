# Each second while outlines exist: removes any whose owner isn't online holding a wand, which hide
# can't catch for a player who logged out with the wand in hand.
tag @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutline] add kalic0re.parkour.boundaryOutlineUnclaimed
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] run function kalic0re:parkour/admin/course_boundary/wand/outline/claim
kill @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineUnclaimed]
execute if entity @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutline,limit=1] run \
    schedule function kalic0re:parkour/admin/course_boundary/wand/outline/sweep 20t replace
