# For place_face: #place$(axis), in tenths, as text pieces in tmp.placeText: its sign, whole part and tenth, so it
# reads exactly, never with a double's stray digits.
$data modify storage kalic0re:parkour tmp.placeText.$(axis)Sign set value ""
$execute if score #place$(axis) kalic0re.parkour.tmp matches ..-1 run data modify storage kalic0re:parkour tmp.placeText.$(axis)Sign set value "-"
$execute if score #place$(axis) kalic0re.parkour.tmp matches ..-1 run scoreboard players operation #place$(axis) kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
$scoreboard players operation #placeWhole kalic0re.parkour.tmp = #place$(axis) kalic0re.parkour.tmp
scoreboard players operation #placeWhole kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour tmp.placeText.$(axis)Whole int 1 run scoreboard players get #placeWhole kalic0re.parkour.tmp
$scoreboard players operation #place$(axis) kalic0re.parkour.tmp %= #10 kalic0re.parkour.tmp
$execute store result storage kalic0re:parkour tmp.placeText.$(axis)Tenth int 1 run scoreboard players get #place$(axis) kalic0re.parkour.tmp
