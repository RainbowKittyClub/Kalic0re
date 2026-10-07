# Scheduled while any display moves or bounces: each one's step, then again next tick. Idle, it costs
# nothing, as nothing is scheduled; effect/display/wake restarts it for one whose chunk loads again.
execute as @e[tag=kalic0re.effect.displayMoving] at @s run function kalic0re:effect/display/move/step
execute as @e[tag=kalic0re.effect.displayBouncing] at @s run function kalic0re:effect/display/bounce/step
data remove storage kalic0re:effect displayMoveStep
data remove storage kalic0re:effect displayBounceStep
execute if entity @e[tag=kalic0re.effect.displayMoving,limit=1] run \
    schedule function kalic0re:effect/display/move/tick 1t replace
execute if entity @e[tag=kalic0re.effect.displayBouncing,limit=1] run \
    schedule function kalic0re:effect/display/move/tick 1t replace
