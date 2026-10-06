# For marker_tick, at the burst point: the blast, a flash in the first colour, and the pattern's sparks flying out.
$execute if data entity @s data{playBlastSound:1b} run playsound $(blastSound) $(soundSource) @a ~ ~ ~ $(soundVolume)
execute store result score #flash kalic0re.firework.tmp run data get entity @s data.colors[0]
# Opaque: the flash's colour is ARGB, and 0xFF000000 as a whole number is -16777216.
scoreboard players remove #flash kalic0re.firework.tmp 16777216
$data modify storage kalic0re:firework tmp.flash set value {particleMode:"$(particleMode)"}
execute store result storage kalic0re:firework tmp.flash.color int 1 run scoreboard players get #flash kalic0re.firework.tmp
function kalic0re:firework/flash with storage kalic0re:firework tmp.flash
data remove storage kalic0re:firework tmp.flash
function kalic0re:firework/sparks {stage:"burst_point"}
