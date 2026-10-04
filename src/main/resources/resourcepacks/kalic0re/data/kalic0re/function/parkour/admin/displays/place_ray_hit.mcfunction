# For place_ray_step, at its first point inside a block: the block, and the step before, outside it, by markers.
scoreboard players set #placeBlockFound kalic0re.parkour.tmp 1
execute align xyz summon minecraft:marker run function kalic0re:parkour/admin/displays/place_read_block
execute positioned ^ ^ ^-0.1 summon minecraft:marker run function kalic0re:parkour/admin/displays/place_read_before
