# As a bouncing display: a speed for one axis within a tenth of the one it was given, drawn afresh each
# time, so the path never settles into a loop. Always from the given speed, so it never drifts, and at
# least 1, so no axis stops.
execute store result score #displayBounceNoisySpeed kalic0re.effect.tmp run random value -100..100
scoreboard players operation #displayBounceNoisySpeed kalic0re.effect.tmp *= @s kalic0re.effect.displayBounceSpeed
scoreboard players operation #displayBounceNoisySpeed kalic0re.effect.tmp /= #1000 kalic0re.effect.tmp
scoreboard players operation #displayBounceNoisySpeed kalic0re.effect.tmp += @s kalic0re.effect.displayBounceSpeed
execute if score #displayBounceNoisySpeed kalic0re.effect.tmp matches ..0 run \
    scoreboard players set #displayBounceNoisySpeed kalic0re.effect.tmp 1
