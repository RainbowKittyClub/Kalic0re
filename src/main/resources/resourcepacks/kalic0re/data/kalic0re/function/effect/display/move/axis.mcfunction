# Along $(axis): how far the move has come by now and the step from where it was, in 1024ths of a block.
# A double holds those exactly, so the steps add up to the distance with nothing lost.
$scoreboard players operation #displayMoveOffset kalic0re.effect.tmp = @s kalic0re.effect.displayMoveBy$(axis)
scoreboard players operation #displayMoveOffset kalic0re.effect.tmp *= #displayMoveProgress kalic0re.effect.tmp
scoreboard players operation #displayMoveOffset kalic0re.effect.tmp /= #1000 kalic0re.effect.tmp
scoreboard players operation #displayMoveStep kalic0re.effect.tmp = #displayMoveOffset kalic0re.effect.tmp
$scoreboard players operation #displayMoveStep kalic0re.effect.tmp -= @s kalic0re.effect.displayMoveMoved$(axis)
$scoreboard players operation @s kalic0re.effect.displayMoveMoved$(axis) = #displayMoveOffset kalic0re.effect.tmp
$execute store result storage kalic0re:effect displayMoveStep.$(key) double 0.0009765625 run \
    scoreboard players get #displayMoveStep kalic0re.effect.tmp
