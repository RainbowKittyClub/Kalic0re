$execute if score #place$(out) kalic0re.parkour.tmp > #placeBest kalic0re.parkour.tmp run scoreboard players set #placeFace kalic0re.parkour.tmp $(face)
$execute if score #place$(out) kalic0re.parkour.tmp > #placeBest kalic0re.parkour.tmp run \
    scoreboard players operation #placeBest kalic0re.parkour.tmp = #place$(out) kalic0re.parkour.tmp
