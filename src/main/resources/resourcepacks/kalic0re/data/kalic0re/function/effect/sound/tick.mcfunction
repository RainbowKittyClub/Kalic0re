# Scheduled while any sound effect plays: each playback's due notes, then again next tick. Idle, it
# costs nothing, as nothing is scheduled.
execute as @e[type=minecraft:marker,tag=kalic0re.effect.soundPlayback] at @s run \
    function kalic0re:effect/sound/playback_tick
execute if entity @e[type=minecraft:marker,tag=kalic0re.effect.soundPlayback,limit=1] run \
    schedule function kalic0re:effect/sound/tick 1t replace
