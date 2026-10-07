# Scheduled while any display moves: each one's step, then again next tick. Idle, it costs nothing, as
# nothing is scheduled.
execute as @e[tag=kalic0re.effect.displayMoving] at @s run function kalic0re:effect/display/move/step
data remove storage kalic0re:effect displayMoveStep
execute if entity @e[tag=kalic0re.effect.displayMoving,limit=1] run \
    schedule function kalic0re:effect/display/move/tick 1t replace
