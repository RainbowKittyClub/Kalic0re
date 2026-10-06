# For place_defaults with no block in reach: level, 3 ahead and 3.5 up from the feet, facing back the way they look,
# whatever their pitch.
execute rotated ~ 0 positioned ^ ^3.5 ^3 summon minecraft:marker run function kalic0re:parkour/admin/displays/place_read_tenths
execute store result score #placeFacing kalic0re.parkour.tmp run data get entity @s Rotation[0] 10
scoreboard players add #placeFacing kalic0re.parkour.tmp 1800
execute if score #placeFacing kalic0re.parkour.tmp matches 1801.. run scoreboard players remove #placeFacing kalic0re.parkour.tmp 3600
data modify storage kalic0re:parkour tmp.placeText set value {}
function kalic0re:parkour/admin/displays/place_tenths {axis:"X"}
function kalic0re:parkour/admin/displays/place_tenths {axis:"Y"}
function kalic0re:parkour/admin/displays/place_tenths {axis:"Z"}
function kalic0re:parkour/admin/displays/place_tenths {axis:"Facing"}
function kalic0re:parkour/admin/displays/place_text_into_args with storage kalic0re:parkour tmp.placeText
function kalic0re:parkour/admin/displays/place_ahead_into_args with storage kalic0re:parkour tmp.placeText
data remove storage kalic0re:parkour tmp.placeText
