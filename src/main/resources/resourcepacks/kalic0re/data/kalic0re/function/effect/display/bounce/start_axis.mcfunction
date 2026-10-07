# Along $(axis), in 1024ths: the span the display's position may cross, so its shape stays in the room
# (none if it doesn't fit), its place in that span, the step that brings it inside, and its velocity.
$execute store result score #displayBounceFrom kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayBounce.from[$(index)]
$execute store result score #displayBounceTo kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayBounce.to[$(index)]
scoreboard players operation #displayBounceRoomLow kalic0re.effect.tmp = #displayBounceFrom kalic0re.effect.tmp
scoreboard players operation #displayBounceRoomLow kalic0re.effect.tmp < #displayBounceTo kalic0re.effect.tmp
scoreboard players operation #displayBounceRoomLow kalic0re.effect.tmp *= #1024 kalic0re.effect.tmp
$scoreboard players operation #displayBounceRoomLow kalic0re.effect.tmp -= #displayBounceLow$(axis) kalic0re.effect.tmp
scoreboard players operation #displayBounceRoomHigh kalic0re.effect.tmp = #displayBounceFrom kalic0re.effect.tmp
scoreboard players operation #displayBounceRoomHigh kalic0re.effect.tmp > #displayBounceTo kalic0re.effect.tmp
scoreboard players add #displayBounceRoomHigh kalic0re.effect.tmp 1
scoreboard players operation #displayBounceRoomHigh kalic0re.effect.tmp *= #1024 kalic0re.effect.tmp
$scoreboard players operation #displayBounceRoomHigh kalic0re.effect.tmp -= #displayBounceHigh$(axis) kalic0re.effect.tmp
$scoreboard players operation @s kalic0re.effect.displayBounceRange$(axis) = #displayBounceRoomHigh kalic0re.effect.tmp
$scoreboard players operation @s kalic0re.effect.displayBounceRange$(axis) -= #displayBounceRoomLow kalic0re.effect.tmp
$execute if score @s kalic0re.effect.displayBounceRange$(axis) matches ..-1 run \
    scoreboard players set @s kalic0re.effect.displayBounceRange$(axis) 0
$execute store result score #displayBouncePlace kalic0re.effect.tmp run data get entity @s Pos[$(index)] 1024
scoreboard players operation #displayBouncePlace kalic0re.effect.tmp -= #displayBounceRoomLow kalic0re.effect.tmp
$scoreboard players operation @s kalic0re.effect.displayBouncePosition$(axis) = #displayBouncePlace kalic0re.effect.tmp
$execute if score @s kalic0re.effect.displayBouncePosition$(axis) matches ..-1 run \
    scoreboard players set @s kalic0re.effect.displayBouncePosition$(axis) 0
$scoreboard players operation @s kalic0re.effect.displayBouncePosition$(axis) < @s kalic0re.effect.displayBounceRange$(axis)
$scoreboard players operation #displayBounceStep kalic0re.effect.tmp = @s kalic0re.effect.displayBouncePosition$(axis)
scoreboard players operation #displayBounceStep kalic0re.effect.tmp -= #displayBouncePlace kalic0re.effect.tmp
$execute store result storage kalic0re:effect displayBounceStep.$(key) double 0.0009765625 run \
    scoreboard players get #displayBounceStep kalic0re.effect.tmp
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) = #displayBounceSpeed kalic0re.effect.tmp
$execute if score @s kalic0re.effect.displayBounceRange$(axis) matches 0 run \
    scoreboard players set @s kalic0re.effect.displayBounceVelocity$(axis) 0
execute store result score #displayBounceCoin kalic0re.effect.tmp run random value 0..1
$execute if score #displayBounceCoin kalic0re.effect.tmp matches 0 run \
    scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) *= #-1 kalic0re.effect.tmp
