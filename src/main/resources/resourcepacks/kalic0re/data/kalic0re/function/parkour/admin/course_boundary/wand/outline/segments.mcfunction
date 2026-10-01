# Edges along $(axis), #side$(axis) tenths long, glow 2 in from each end, or half the side if shorter:
# g$(axis), each end's length, and e$(axis), where the far end starts.
$scoreboard players operation #glow kalic0re.parkour.tmp = #side$(axis) kalic0re.parkour.tmp
scoreboard players operation #glow kalic0re.parkour.tmp /= #2 kalic0re.parkour.tmp
execute if score #glow kalic0re.parkour.tmp matches 21.. run scoreboard players set #glow kalic0re.parkour.tmp 20
$execute store result storage kalic0re:parkour tmp.outline.box.g$(axis) double 0.1 run scoreboard players get #glow kalic0re.parkour.tmp
$scoreboard players operation #glowEnd kalic0re.parkour.tmp = #side$(axis) kalic0re.parkour.tmp
scoreboard players operation #glowEnd kalic0re.parkour.tmp -= #glow kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour tmp.outline.box.e$(axis) double 0.1 run scoreboard players get #glowEnd kalic0re.parkour.tmp
