# Past the far end of the span along $(axis): what overshot comes back the other way, as does the velocity.
$scoreboard players operation #displayBounceFold kalic0re.effect.tmp = @s kalic0re.effect.displayBounceRange$(axis)
scoreboard players operation #displayBounceFold kalic0re.effect.tmp *= #2 kalic0re.effect.tmp
scoreboard players operation #displayBounceFold kalic0re.effect.tmp -= #displayBounceNext kalic0re.effect.tmp
scoreboard players operation #displayBounceNext kalic0re.effect.tmp = #displayBounceFold kalic0re.effect.tmp
execute if score #displayBounceNext kalic0re.effect.tmp matches ..-1 run scoreboard players set #displayBounceNext kalic0re.effect.tmp 0
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) *= #-1 kalic0re.effect.tmp
scoreboard players add @s kalic0re.effect.displayBounceWallHits 1
