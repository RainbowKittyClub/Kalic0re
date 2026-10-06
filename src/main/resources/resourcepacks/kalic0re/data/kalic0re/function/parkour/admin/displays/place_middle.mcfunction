# For place_face, along $(axis), in tenths: the block's middle, or 0.1 out past its low or high side when the face is
# that side ($(low) or $(high)).
$scoreboard players operation #place$(axis) kalic0re.parkour.tmp = #placeBlock$(axis) kalic0re.parkour.tmp
$scoreboard players operation #place$(axis) kalic0re.parkour.tmp *= #10 kalic0re.parkour.tmp
$execute if score #placeFace kalic0re.parkour.tmp matches $(low) run scoreboard players remove #place$(axis) kalic0re.parkour.tmp 1
$execute if score #placeFace kalic0re.parkour.tmp matches $(high) run scoreboard players add #place$(axis) kalic0re.parkour.tmp 11
$execute unless score #placeFace kalic0re.parkour.tmp matches $(low)..$(high) run scoreboard players add #place$(axis) kalic0re.parkour.tmp 5
