# The move done: the display's own glide length back, and its move state cleared.
data modify entity @s teleport_duration set from entity @s data."kalic0re.effect.teleportDurationBeforeMove"
data remove entity @s data."kalic0re.effect.teleportDurationBeforeMove"
tag @s remove kalic0re.effect.displayMoving
scoreboard players reset @s kalic0re.effect.displayMoveTick
scoreboard players reset @s kalic0re.effect.displayMoveTicks
scoreboard players reset @s kalic0re.effect.displayMoveSmooth
scoreboard players reset @s kalic0re.effect.displayMoveByX
scoreboard players reset @s kalic0re.effect.displayMoveByY
scoreboard players reset @s kalic0re.effect.displayMoveByZ
scoreboard players reset @s kalic0re.effect.displayMoveMovedX
scoreboard players reset @s kalic0re.effect.displayMoveMovedY
scoreboard players reset @s kalic0re.effect.displayMoveMovedZ
