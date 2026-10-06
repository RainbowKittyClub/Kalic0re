# For read: the $(box) box, empty for its default or a sound's id, as edited.$(box).
$data modify storage kalic0re:firework edited.$(box) set value ""
$execute if data storage kalic0re:firework input.texts{$(box):""} run return 1
data modify storage kalic0re:text check set value ""
$data modify storage kalic0re:text check set from storage kalic0re:firework input.texts.$(box)
execute unless function kalic0re:text/only_resource_id run return fail
data modify storage kalic0re:firework tmp.sound set value {}
$data modify storage kalic0re:firework tmp.sound.sound set from storage kalic0re:firework input.texts.$(box)
scoreboard players set #reads kalic0re.firework.tmp 0
execute store result score #reads kalic0re.firework.tmp run function kalic0re:firework/editor/sound_reads with storage kalic0re:firework tmp.sound
$data modify storage kalic0re:firework edited.$(box) set from storage kalic0re:firework tmp.sound.sound
data remove storage kalic0re:firework tmp.sound
return run scoreboard players get #reads kalic0re.firework.tmp
