# For place_face: face $(face) when the step before is out past it and the look crossed its plane last so far, the
# most out for the look's pace along $(axis): out / along, compared as out × best's along > best × along.
$execute unless score #place$(out) kalic0re.parkour.tmp matches 1.. run return 0
$scoreboard players operation #placeCross kalic0re.parkour.tmp = #place$(out) kalic0re.parkour.tmp
scoreboard players operation #placeCross kalic0re.parkour.tmp *= #placeBestAlong kalic0re.parkour.tmp
scoreboard players operation #placeBestCross kalic0re.parkour.tmp = #placeBest kalic0re.parkour.tmp
$scoreboard players operation #placeBestCross kalic0re.parkour.tmp *= #placeAlong$(axis) kalic0re.parkour.tmp
execute if score #placeCross kalic0re.parkour.tmp <= #placeBestCross kalic0re.parkour.tmp run return 0
$scoreboard players set #placeFace kalic0re.parkour.tmp $(face)
$scoreboard players operation #placeBest kalic0re.parkour.tmp = #place$(out) kalic0re.parkour.tmp
$scoreboard players operation #placeBestAlong kalic0re.parkour.tmp = #placeAlong$(axis) kalic0re.parkour.tmp
