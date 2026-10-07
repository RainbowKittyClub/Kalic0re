# Past the far end of the span along $(axis): what overshot comes back the other way, and the velocity
# turns back at a fresh speed.
$scoreboard players operation #displayBounceFold kalic0re.effect.tmp = @s kalic0re.effect.displayBounceRange$(axis)
scoreboard players operation #displayBounceFold kalic0re.effect.tmp *= #2 kalic0re.effect.tmp
scoreboard players operation #displayBounceFold kalic0re.effect.tmp -= #displayBounceNext kalic0re.effect.tmp
scoreboard players operation #displayBounceNext kalic0re.effect.tmp = #displayBounceFold kalic0re.effect.tmp
execute if score #displayBounceNext kalic0re.effect.tmp matches ..-1 run scoreboard players set #displayBounceNext kalic0re.effect.tmp 0
function kalic0re:effect/display/bounce/noisy_speed
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) = #displayBounceNoisySpeed kalic0re.effect.tmp
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) *= #-1 kalic0re.effect.tmp
scoreboard players add @s kalic0re.effect.displayBounceWallHits 1
