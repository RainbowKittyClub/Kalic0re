# Past the near end of the span along $(axis): what overshot comes back the other way, as does the velocity.
scoreboard players operation #displayBounceNext kalic0re.effect.tmp *= #-1 kalic0re.effect.tmp
$scoreboard players operation #displayBounceNext kalic0re.effect.tmp < @s kalic0re.effect.displayBounceRange$(axis)
$scoreboard players operation @s kalic0re.effect.displayBounceVelocity$(axis) *= #-1 kalic0re.effect.tmp
