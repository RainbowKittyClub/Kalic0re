# Sound effects: the ticks since each playback began, kept on its marker; and their arithmetic.
scoreboard objectives add kalic0re.effect.soundPlaybackTick dummy
scoreboard objectives add kalic0re.effect.tmp dummy
scoreboard players set #50 kalic0re.effect.tmp 50
# A playback a reload or restart cut into carries on from its marker.
schedule function kalic0re:effect/sound/tick 1t replace
# Moving displays: each one's move, kept on it: ticks so far and in all, whether it eases, and the
# distance along each axis to cover and covered so far, in 1024ths of a block.
scoreboard objectives add kalic0re.effect.displayMoveTick dummy
scoreboard objectives add kalic0re.effect.displayMoveTicks dummy
scoreboard objectives add kalic0re.effect.displayMoveSmooth dummy
scoreboard objectives add kalic0re.effect.displayMoveByX dummy
scoreboard objectives add kalic0re.effect.displayMoveByY dummy
scoreboard objectives add kalic0re.effect.displayMoveByZ dummy
scoreboard objectives add kalic0re.effect.displayMoveMovedX dummy
scoreboard objectives add kalic0re.effect.displayMoveMovedY dummy
scoreboard objectives add kalic0re.effect.displayMoveMovedZ dummy
scoreboard players set #2 kalic0re.effect.tmp 2
scoreboard players set #1000 kalic0re.effect.tmp 1000
# A move a reload or restart cut into carries on from where its display is.
schedule function kalic0re:effect/display/move/tick 1t replace
