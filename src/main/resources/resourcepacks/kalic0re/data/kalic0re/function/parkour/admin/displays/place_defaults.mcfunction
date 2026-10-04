# As a player opening the Place dialog: Position and Rotation's starting text in kalic0re:dialog args. At a block
# within 64, 0.1 out from the middle of the face they look at, the board flat on it and facing out of it, toward
# them; at none, 3 ahead and facing them.
data modify storage kalic0re:dialog args.place_position set value "^ ^ ^3"
data modify storage kalic0re:dialog args.place_rotation set value "~180 0"
scoreboard players set #placeBlockFound kalic0re.parkour.tmp 0
scoreboard players set #rayStepsLeft kalic0re.parkour.tmp 640
execute anchored eyes positioned ^ ^ ^ anchored feet run function kalic0re:parkour/admin/displays/place_ray_step
execute if score #placeBlockFound kalic0re.parkour.tmp matches 1 run function kalic0re:parkour/admin/displays/place_face
