# execute as <display> run function kalic0re:effect/display/animate_bounce_display {from:"<x y z>",to:"<x y z>",speed:<blocks a second>}
# e.g. execute as @e[tag=logo,limit=1] run function … {from:"10 64 10",to:"20 70 20",speed:3}
# Bounces this display, and whatever rides it, around the room of blocks from–to like the DVD logo:
# speed blocks a second along each axis, so diagonally, turning back off each wall it reaches. What it
# and its riders draw, blocks or items, stays whole inside the room; anything else counts as a point.
# Starts from where it is, pulled inside if outside, in a random diagonal, and runs until
# animate_stop_display, a new move or bounce, or removal. Coordinates read as in a /fill of your own.
# Fails unless speed is above 0. Works within 2 million blocks of 0 0 0. On a tick it turns off a wall,
# its score kalic0re.effect.displayBounceWallHits counts the walls, 3 at a corner, and is 0 otherwise:
# execute as @e[tag=logo,scores={kalic0re.effect.displayBounceWallHits=1..}] run …
$data modify storage kalic0re:effect displayBounce set value {speed:$(speed)d}
execute store result score #displayBounceSpeed kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayBounce.speed 51.2
execute unless score #displayBounceSpeed kalic0re.effect.tmp matches 1.. run \
    return run function kalic0re:effect/display/bounce/refuse
$execute positioned $(from) align xyz summon minecraft:marker run \
    function kalic0re:effect/display/bounce/corner {corner:"from"}
$execute positioned $(to) align xyz summon minecraft:marker run \
    function kalic0re:effect/display/bounce/corner {corner:"to"}
execute if entity @s[tag=kalic0re.effect.displayMoving] run function kalic0re:effect/display/move/finish
execute if entity @s[tag=kalic0re.effect.displayBouncing] run function kalic0re:effect/display/bounce/finish
function kalic0re:effect/display/bounce/shapes
function kalic0re:effect/display/bounce/start_axis {axis:"X",key:"x",index:0}
function kalic0re:effect/display/bounce/start_axis {axis:"Y",key:"y",index:1}
function kalic0re:effect/display/bounce/start_axis {axis:"Z",key:"z",index:2}
execute at @s run function kalic0re:effect/display/move/teleport with storage kalic0re:effect displayBounceStep
data modify entity @s data."kalic0re.effect.teleportDurationBeforeMove" set from entity @s teleport_duration
data modify entity @s teleport_duration set value 2
scoreboard players set @s kalic0re.effect.displayBounceWallHits 0
tag @s add kalic0re.effect.displayBouncing
data remove storage kalic0re:effect displayBounce
data remove storage kalic0re:effect displayBounceShape
data remove storage kalic0re:effect displayBounceStep
schedule function kalic0re:effect/display/move/tick 1t replace
