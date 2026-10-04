# The look ray's hit for place_defaults: the block, and the step before, outside it, by markers.
scoreboard players set #placeBlockFound kalic0re.parkour.tmp 1
execute align xyz summon minecraft:marker run function kalic0re:parkour/admin/read_block_of_marker {into:"placeBlock"}
execute store result score #placeBlockX kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.placeBlock[0]
execute store result score #placeBlockY kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.placeBlock[1]
execute store result score #placeBlockZ kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.placeBlock[2]
data remove storage kalic0re:parkour tmp.placeBlock
execute positioned ^ ^ ^-0.1 summon minecraft:marker run function kalic0re:parkour/admin/displays/place_read_before
