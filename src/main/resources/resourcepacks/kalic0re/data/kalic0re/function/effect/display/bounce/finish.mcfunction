# The bounce stopped: the display's own glide length back, and its bounce state cleared.
data modify entity @s teleport_duration set from entity @s data."kalic0re.effect.teleportDurationBeforeMove"
data remove entity @s data."kalic0re.effect.teleportDurationBeforeMove"
tag @s remove kalic0re.effect.displayBouncing
scoreboard players reset @s kalic0re.effect.displayBouncePositionX
scoreboard players reset @s kalic0re.effect.displayBouncePositionY
scoreboard players reset @s kalic0re.effect.displayBouncePositionZ
scoreboard players reset @s kalic0re.effect.displayBounceRangeX
scoreboard players reset @s kalic0re.effect.displayBounceRangeY
scoreboard players reset @s kalic0re.effect.displayBounceRangeZ
scoreboard players reset @s kalic0re.effect.displayBounceVelocityX
scoreboard players reset @s kalic0re.effect.displayBounceVelocityY
scoreboard players reset @s kalic0re.effect.displayBounceVelocityZ
scoreboard players reset @s kalic0re.effect.displayBounceWallHits
scoreboard players reset @s kalic0re.effect.displayBounceSpeed
