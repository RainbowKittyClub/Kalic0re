# Displays just tagged to clear leave the outline, so nothing finds them as an owner's again, and wait
# for clear_queued_displays. Tags never reach a client: only the kills cost, and those are spread out.
tag @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineToClear] \
    remove kalic0re.parkour.boundaryOutline
scoreboard players set #boundaryOutlineDisplaysToClear kalic0re.parkour.tmp 1
