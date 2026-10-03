execute unless data entity @s data.notes[0] run return 0
execute store result score #soundEffectDue kalic0re.effect.tmp run data get entity @s data.notes[0].due
execute if score #soundEffectDue kalic0re.effect.tmp > @s kalic0re.effect.soundPlaybackTick run return 0
data modify storage kalic0re:effect soundNote set from entity @s data.notes[0]
data modify storage kalic0re:effect soundNote.listenerTag set from entity @s data.listenerTag
function kalic0re:effect/sound/play_note with storage kalic0re:effect soundNote
data remove entity @s data.notes[0]
function kalic0re:effect/sound/play_due
