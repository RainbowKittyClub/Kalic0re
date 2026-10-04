# As a marker summoned at a block's corner: that block, in whole blocks, as tmp.$(into), then gone.
$data modify storage kalic0re:parkour tmp.$(into) set value [0,0,0]
$execute store result storage kalic0re:parkour tmp.$(into)[0] int 1 run data get entity @s Pos[0]
$execute store result storage kalic0re:parkour tmp.$(into)[1] int 1 run data get entity @s Pos[1]
$execute store result storage kalic0re:parkour tmp.$(into)[2] int 1 run data get entity @s Pos[2]
kill @s
