# As a playback's marker, at it: every note now due, then the tick counted; with none left, it ends.
function kalic0re:effect/sound/play_due
data remove storage kalic0re:effect soundNote
execute unless data entity @s data.notes[0] run return run function kalic0re:effect/sound/finish with entity @s data
scoreboard players add @s kalic0re.effect.soundPlaybackTick 1
