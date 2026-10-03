# Sound effects: the ticks since each playback began, kept on its marker; and their arithmetic.
scoreboard objectives add kalic0re.effect.soundPlaybackTick dummy
scoreboard objectives add kalic0re.effect.tmp dummy
scoreboard players set #50 kalic0re.effect.tmp 50
# A playback a reload or restart cut into carries on from its marker.
schedule function kalic0re:effect/sound/tick 1t replace
