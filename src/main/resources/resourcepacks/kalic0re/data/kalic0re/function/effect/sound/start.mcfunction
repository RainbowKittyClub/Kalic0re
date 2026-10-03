# As playback $(id)'s new marker: $(effect)'s notes, each with the tick it's due on, kept in the marker's
# data. The notes due at once play now; tick plays the rest.
tag @s add kalic0re.effect.soundPlayback
$data modify entity @s data.listenerTag set value "kalic0re.effect.soundPlayback$(id)Listener"
data modify storage kalic0re:effect sound set value {notes:[]}
function kalic0re:effect/sound/load_notes with storage kalic0re:effect soundPlayback
data modify storage kalic0re:effect soundQueue set value []
scoreboard players set #soundEffectMilliseconds kalic0re.effect.tmp 0
function kalic0re:effect/sound/queue_next
data modify entity @s data.notes set from storage kalic0re:effect soundQueue
data remove storage kalic0re:effect sound
data remove storage kalic0re:effect soundQueue
scoreboard players set @s kalic0re.effect.soundPlaybackTick 0
function kalic0re:effect/sound/playback_tick
schedule function kalic0re:effect/sound/tick 1t replace
