# In the tick tag: every 16 ticks, restarts the stepping for a moving or bouncing display that stopped
# when its chunk unloaded and is loaded again; a bounce has no end of its own to wait for.
execute if score #16ticks kalic0re.timer matches 0 if entity @e[tag=kalic0re.effect.displayMoving,limit=1] run \
    schedule function kalic0re:effect/display/move/tick 1t replace
execute if score #16ticks kalic0re.timer matches 0 if entity @e[tag=kalic0re.effect.displayBouncing,limit=1] run \
    schedule function kalic0re:effect/display/move/tick 1t replace
