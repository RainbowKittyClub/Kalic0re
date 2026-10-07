# execute as <display> run function kalic0re:effect/display/animate_move_display {ramp:"<linear|smooth>",dx:<x>,dy:<y>,dz:<z>,ticks:<n>}
# e.g. execute as @e[tag=gate,limit=1] run function … {ramp:"smooth",dx:0,dy:-4,dz:0,ticks:40}
# Moves this display, and whatever rides it, by dx dy dz over ticks: at a steady speed when linear,
# easing in and out when smooth. It is teleported a step each tick, which clients glide over. A new
# move replaces one in progress and starts from where the display is. Fails on any other ramp, or on
# ticks under 1. Covers up to about 2000 blocks along each axis.
$data modify storage kalic0re:effect displayMove set value {ramp:"$(ramp)",ticks:$(ticks),by:[$(dx)d,$(dy)d,$(dz)d]}
execute store result score #displayMoveTicks kalic0re.effect.tmp run data get storage kalic0re:effect displayMove.ticks
execute unless score #displayMoveTicks kalic0re.effect.tmp matches 1.. run \
    return run function kalic0re:effect/display/move/refuse
execute unless data storage kalic0re:effect displayMove{ramp:"linear"} \
    unless data storage kalic0re:effect displayMove{ramp:"smooth"} run \
    return run function kalic0re:effect/display/move/refuse
execute unless entity @s[tag=kalic0re.effect.displayMoving] run \
    data modify entity @s data."kalic0re.effect.teleportDurationBeforeMove" set from entity @s teleport_duration
data modify entity @s teleport_duration set value 2
scoreboard players set @s kalic0re.effect.displayMoveTick 0
scoreboard players operation @s kalic0re.effect.displayMoveTicks = #displayMoveTicks kalic0re.effect.tmp
execute store success score @s kalic0re.effect.displayMoveSmooth if data storage kalic0re:effect displayMove{ramp:"smooth"}
execute store result score @s kalic0re.effect.displayMoveByX run data get storage kalic0re:effect displayMove.by[0] 1024
execute store result score @s kalic0re.effect.displayMoveByY run data get storage kalic0re:effect displayMove.by[1] 1024
execute store result score @s kalic0re.effect.displayMoveByZ run data get storage kalic0re:effect displayMove.by[2] 1024
scoreboard players set @s kalic0re.effect.displayMoveMovedX 0
scoreboard players set @s kalic0re.effect.displayMoveMovedY 0
scoreboard players set @s kalic0re.effect.displayMoveMovedZ 0
tag @s add kalic0re.effect.displayMoving
data remove storage kalic0re:effect displayMove
schedule function kalic0re:effect/display/move/tick 1t replace
