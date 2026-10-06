# For read: the sliders, toggles and choices, each as its setting's type, ticks whole and at least 1; fails on a
# pattern or visibility the dialog doesn't offer.
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:firework input.values.pattern
execute unless function kalic0re:text/only_identifier run return fail
scoreboard players set #known kalic0re.firework.tmp 0
execute store result score #known kalic0re.firework.tmp run \
    function kalic0re:firework/editor/pattern_known with storage kalic0re:firework input.values
execute if score #known kalic0re.firework.tmp matches 0 run return fail
data modify storage kalic0re:firework edited.pattern set from storage kalic0re:firework input.values.pattern
execute unless data storage kalic0re:firework input.values{particleMode:"force"} \
    unless data storage kalic0re:firework input.values{particleMode:"normal"} run return fail
data modify storage kalic0re:firework edited.particleMode set from storage kalic0re:firework input.values.particleMode
function kalic0re:firework/editor/read_thousandths {key:"radius",type:"double"}
function kalic0re:firework/editor/read_thousandths {key:"dustScale",type:"float"}
function kalic0re:firework/editor/read_thousandths {key:"sparkSpeed",type:"double"}
function kalic0re:firework/editor/read_thousandths {key:"twinkleSpread",type:"double"}
function kalic0re:firework/editor/read_thousandths {key:"soundVolume",type:"float"}
execute store result storage kalic0re:firework edited.twinkleSparks int 1 run data get storage kalic0re:firework input.values.twinkleSparks
function kalic0re:firework/editor/read_ticks {key:"riseTicks"}
function kalic0re:firework/editor/read_ticks {key:"burstTicks"}
function kalic0re:firework/editor/read_ticks {key:"twinkleDelayTicks"}
data modify storage kalic0re:firework edited.hasTrail set from storage kalic0re:firework input.values.hasTrail
data modify storage kalic0re:firework edited.hasTwinkle set from storage kalic0re:firework input.values.hasTwinkle
data modify storage kalic0re:firework edited.playLaunchSound set from storage kalic0re:firework input.values.playLaunchSound
data modify storage kalic0re:firework edited.playBlastSound set from storage kalic0re:firework input.values.playBlastSound
data modify storage kalic0re:firework edited.playTwinkleSound set from storage kalic0re:firework input.values.playTwinkleSound
return 1
