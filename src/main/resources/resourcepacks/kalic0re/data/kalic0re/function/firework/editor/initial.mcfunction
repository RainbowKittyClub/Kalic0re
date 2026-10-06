# For open: slider $(key) starting at tmp.editing.$(path), held within the slider's own start and end (thousandths),
# outside which the dialog wouldn't open.
$execute store result score #initial kalic0re.firework.tmp run data get storage kalic0re:firework tmp.editing.$(path) 1000
$execute store result score #sliderStart kalic0re.firework.tmp run \
    data get storage kalic0re:dialog built.create_firework.inputs[{key:"$(key)"}].start 1000
$execute store result score #sliderEnd kalic0re.firework.tmp run \
    data get storage kalic0re:dialog built.create_firework.inputs[{key:"$(key)"}].end 1000
scoreboard players operation #initial kalic0re.firework.tmp > #sliderStart kalic0re.firework.tmp
scoreboard players operation #initial kalic0re.firework.tmp < #sliderEnd kalic0re.firework.tmp
$execute store result storage kalic0re:dialog built.create_firework.inputs[{key:"$(key)"}].initial float 0.001 run \
    scoreboard players get #initial kalic0re.firework.tmp
