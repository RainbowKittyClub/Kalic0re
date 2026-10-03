# Gives the first of sound.notes the tick it's due on, from the delays so far, and moves it to soundQueue.
# Its delay is the wait after the note before it. In milliseconds, rounded to the nearest tick of 50.
execute unless data storage kalic0re:effect sound.notes[0] run return 0
execute store result score #soundEffectDelay kalic0re.effect.tmp run data get storage kalic0re:effect sound.notes[0].delay 1000
scoreboard players operation #soundEffectMilliseconds kalic0re.effect.tmp += #soundEffectDelay kalic0re.effect.tmp
scoreboard players operation #soundEffectDue kalic0re.effect.tmp = #soundEffectMilliseconds kalic0re.effect.tmp
scoreboard players add #soundEffectDue kalic0re.effect.tmp 25
scoreboard players operation #soundEffectDue kalic0re.effect.tmp /= #50 kalic0re.effect.tmp
execute store result storage kalic0re:effect sound.notes[0].due int 1 run \
    scoreboard players get #soundEffectDue kalic0re.effect.tmp
data modify storage kalic0re:effect soundQueue append from storage kalic0re:effect sound.notes[0]
data remove storage kalic0re:effect sound.notes[0]
function kalic0re:effect/sound/queue_next
