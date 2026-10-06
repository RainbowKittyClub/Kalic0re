# For place_face, along $(axis): how far the step before lies below the block's low side (#placeLow) and above its
# high side (#placeHigh), in thousandths; negative when within.
$scoreboard players operation #placeLow$(axis) kalic0re.parkour.tmp = #placeBlock$(axis) kalic0re.parkour.tmp
$scoreboard players operation #placeLow$(axis) kalic0re.parkour.tmp *= #1000 kalic0re.parkour.tmp
$scoreboard players operation #placeHigh$(axis) kalic0re.parkour.tmp = #placeBefore$(axis) kalic0re.parkour.tmp
$scoreboard players operation #placeHigh$(axis) kalic0re.parkour.tmp -= #placeLow$(axis) kalic0re.parkour.tmp
$scoreboard players remove #placeHigh$(axis) kalic0re.parkour.tmp 1000
$scoreboard players operation #placeLow$(axis) kalic0re.parkour.tmp -= #placeBefore$(axis) kalic0re.parkour.tmp
