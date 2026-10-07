# Past the near end of the span along $(axis): what overshot comes back the other way, and the velocity
# turns back at a fresh speed.
scoreboard players operation #displayBounceNext kalic0re.effect.tmp *= #-1 kalic0re.effect.tmp
$scoreboard players operation #displayBounceNext kalic0re.effect.tmp < @s kalic0re.effect.displayBounceRange$(axis)
function kalic0re:effect/display/bounce/noisy_speed
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) = #displayBounceNoisySpeed kalic0re.effect.tmp
scoreboard players add @s kalic0re.effect.displayBounceWallHits 1
