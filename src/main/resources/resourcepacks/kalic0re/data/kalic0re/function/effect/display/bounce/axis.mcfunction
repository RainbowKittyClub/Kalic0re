# Along $(axis): the next place, turned back off a wall it would pass, and the step there, in 1024ths.
$scoreboard players operation #displayBounceNext kalic0re.effect.tmp = @s kalic0re.effect.displayBouncePosition$(axis)
$scoreboard players operation #displayBounceNext kalic0re.effect.tmp += @s kalic0re.effect.displayBounceVelocity$(axis)
$execute if score #displayBounceNext kalic0re.effect.tmp > @s kalic0re.effect.displayBounceRange$(axis) run \
    function kalic0re:effect/display/bounce/off_far_wall {axis:"$(axis)"}
$execute if score #displayBounceNext kalic0re.effect.tmp matches ..-1 run \
    function kalic0re:effect/display/bounce/off_near_wall {axis:"$(axis)"}
scoreboard players operation #displayBounceStep kalic0re.effect.tmp = #displayBounceNext kalic0re.effect.tmp
$scoreboard players operation #displayBounceStep kalic0re.effect.tmp -= @s kalic0re.effect.displayBouncePosition$(axis)
$scoreboard players operation @s kalic0re.effect.displayBouncePosition$(axis) = #displayBounceNext kalic0re.effect.tmp
$execute store result storage kalic0re:effect displayBounceStep.$(key) double 0.0009765625 run \
    scoreboard players get #displayBounceStep kalic0re.effect.tmp
