data modify storage kalic0re:parkour tmp.wand.pos set value [0,0,0]
execute store result storage kalic0re:parkour tmp.wand.pos[0] int 1 run data get entity @s Pos[0]
execute store result storage kalic0re:parkour tmp.wand.pos[1] int 1 run data get entity @s Pos[1]
execute store result storage kalic0re:parkour tmp.wand.pos[2] int 1 run data get entity @s Pos[2]
kill @s
