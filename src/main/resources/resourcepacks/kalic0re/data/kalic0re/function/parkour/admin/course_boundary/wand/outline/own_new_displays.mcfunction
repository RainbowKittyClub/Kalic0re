# Marks the displays just summoned for this holder's outline with their owner number.
scoreboard players operation @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineNew] \
    kalic0re.parkour.boundaryOutlineOwner = @s kalic0re.parkour.boundaryOutlineOwner
tag @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineNew] remove kalic0re.parkour.boundaryOutlineNew
