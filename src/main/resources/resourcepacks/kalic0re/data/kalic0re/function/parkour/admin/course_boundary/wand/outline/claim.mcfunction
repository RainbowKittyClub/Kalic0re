scoreboard players operation #owner kalic0re.parkour.tmp = @s kalic0re.parkour.boundaryOutlineOwner
execute as @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineUnclaimed] \
    if score @s kalic0re.parkour.boundaryOutlineOwner = #owner kalic0re.parkour.tmp run \
    tag @s remove kalic0re.parkour.boundaryOutlineUnclaimed
