# As the display: the box that it and its riders draw, from its position, in 1024ths along each axis;
# none at all leaves a point. A root of create_from_blocks draws air, so only its riders count.
scoreboard players set #displayBounceShapes kalic0re.effect.tmp 0
scoreboard players set #displayBounceLowX kalic0re.effect.tmp 0
scoreboard players set #displayBounceLowY kalic0re.effect.tmp 0
scoreboard players set #displayBounceLowZ kalic0re.effect.tmp 0
scoreboard players set #displayBounceHighX kalic0re.effect.tmp 0
scoreboard players set #displayBounceHighY kalic0re.effect.tmp 0
scoreboard players set #displayBounceHighZ kalic0re.effect.tmp 0
function kalic0re:effect/display/bounce/shape_of
execute on passengers run function kalic0re:effect/display/bounce/shape_of
