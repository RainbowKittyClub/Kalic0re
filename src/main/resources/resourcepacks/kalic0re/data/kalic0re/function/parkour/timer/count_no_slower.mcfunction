# Counts the entries at the front of tmp.scan, sorted fastest first, that are no slower than #ticks.
execute unless data storage kalic0re:parkour tmp.scan[0] run return 0
execute store result score #entryTicks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.scan[0].ticks
execute if score #entryTicks kalic0re.parkour.tmp > #ticks kalic0re.parkour.tmp run return 0
scoreboard players add #index kalic0re.parkour.tmp 1
data remove storage kalic0re:parkour tmp.scan[0]
function kalic0re:parkour/timer/count_no_slower
