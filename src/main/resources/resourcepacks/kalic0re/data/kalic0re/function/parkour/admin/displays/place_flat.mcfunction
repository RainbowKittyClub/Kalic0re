# For place_face, a floor (4) or ceiling (3): flat, facing up or down, turned to the nearest quarter so its top points
# away from the player. Facing up, a board's top is the way it's turned round; facing down, the way it's turned.
execute store result score #placeYaw kalic0re.parkour.tmp run data get entity @s Rotation[0]
execute if score #placeFace kalic0re.parkour.tmp matches 4 run scoreboard players add #placeYaw kalic0re.parkour.tmp 180
scoreboard players add #placeYaw kalic0re.parkour.tmp 45
scoreboard players operation #placeYaw kalic0re.parkour.tmp /= #90 kalic0re.parkour.tmp
scoreboard players operation #placeYaw kalic0re.parkour.tmp %= #4 kalic0re.parkour.tmp
data modify storage kalic0re:parkour tmp.placeFlat set value {pitch:"90.0"}
execute if score #placeFace kalic0re.parkour.tmp matches 4 run data modify storage kalic0re:parkour tmp.placeFlat.pitch set value "-90.0"
execute if score #placeYaw kalic0re.parkour.tmp matches 0 run data modify storage kalic0re:parkour tmp.placeFlat.yaw set value "0.0"
execute if score #placeYaw kalic0re.parkour.tmp matches 1 run data modify storage kalic0re:parkour tmp.placeFlat.yaw set value "90.0"
execute if score #placeYaw kalic0re.parkour.tmp matches 2 run data modify storage kalic0re:parkour tmp.placeFlat.yaw set value "180.0"
execute if score #placeYaw kalic0re.parkour.tmp matches 3 run data modify storage kalic0re:parkour tmp.placeFlat.yaw set value "-90.0"
function kalic0re:parkour/admin/displays/place_flat_into_args with storage kalic0re:parkour tmp.placeFlat
data remove storage kalic0re:parkour tmp.placeFlat
