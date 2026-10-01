# Three boundaries' worth a tick, the rate they're drawn at: each kill is a packet to every player in
# range, so a whole outline at once makes one long tick.
kill @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineToClear,limit=90]
execute unless entity @e[type=#kalic0re:parkour/boundary_outline_displays,tag=kalic0re.parkour.boundaryOutlineToClear,limit=1] run \
    scoreboard players set #boundaryOutlineDisplaysToClear kalic0re.parkour.tmp 0
