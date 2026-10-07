# As a moving display, at it: its progress, in thousandths, eased if smooth, and the step there. The tick
# after the last step ends it, so the glide length is put back only once the last glide is under way.
scoreboard players add @s kalic0re.effect.displayMoveTick 1
scoreboard players operation #displayMoveProgress kalic0re.effect.tmp = @s kalic0re.effect.displayMoveTick
scoreboard players operation #displayMoveProgress kalic0re.effect.tmp < @s kalic0re.effect.displayMoveTicks
scoreboard players operation #displayMoveProgress kalic0re.effect.tmp *= #1000 kalic0re.effect.tmp
scoreboard players operation #displayMoveProgress kalic0re.effect.tmp /= @s kalic0re.effect.displayMoveTicks
execute if score @s kalic0re.effect.displayMoveSmooth matches 1 run function kalic0re:effect/display/move/ease
function kalic0re:effect/display/move/axis {axis:"X",key:"x"}
function kalic0re:effect/display/move/axis {axis:"Y",key:"y"}
function kalic0re:effect/display/move/axis {axis:"Z",key:"z"}
function kalic0re:effect/display/move/teleport with storage kalic0re:effect displayMoveStep
execute if score @s kalic0re.effect.displayMoveTick > @s kalic0re.effect.displayMoveTicks run \
    function kalic0re:effect/display/move/finish
