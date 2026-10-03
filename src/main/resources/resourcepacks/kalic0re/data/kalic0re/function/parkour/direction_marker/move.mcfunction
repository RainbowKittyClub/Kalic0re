# As a direction marker: its translation is its base plus this tick's offset along the way it points, in
# ten-thousandths. Any write to a display restarts its interpolation, so the client smooths each tick's step.
# Without scores it's new, or a copy, which carries its tags but never its scores. Tagged Ready, it's from the
# first version of this, which kept its base only in scores.
execute unless score @s kalic0re.parkour.directionMarkerDirectionX matches -2147483648.. run \
    function kalic0re:parkour/direction_marker/setup
execute if entity @s[tag=kalic0re.parkour.directionMarkerReady] run function kalic0re:parkour/direction_marker/setup
# Its rotations again, so turning it takes effect within 16 ticks.
execute if score #16ticks kalic0re.timer matches 0 run function kalic0re:parkour/direction_marker/refresh_direction
scoreboard players operation #directionMarkerX kalic0re.parkour.tmp = @s kalic0re.parkour.directionMarkerDirectionX
scoreboard players operation #directionMarkerX kalic0re.parkour.tmp *= #directionMarkerOffset kalic0re.parkour.tmp
scoreboard players operation #directionMarkerX kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #directionMarkerX kalic0re.parkour.tmp += @s kalic0re.parkour.directionMarkerBaseX
scoreboard players operation #directionMarkerY kalic0re.parkour.tmp = @s kalic0re.parkour.directionMarkerDirectionY
scoreboard players operation #directionMarkerY kalic0re.parkour.tmp *= #directionMarkerOffset kalic0re.parkour.tmp
scoreboard players operation #directionMarkerY kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #directionMarkerY kalic0re.parkour.tmp += @s kalic0re.parkour.directionMarkerBaseY
scoreboard players operation #directionMarkerZ kalic0re.parkour.tmp = @s kalic0re.parkour.directionMarkerDirectionZ
scoreboard players operation #directionMarkerZ kalic0re.parkour.tmp *= #directionMarkerOffset kalic0re.parkour.tmp
scoreboard players operation #directionMarkerZ kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #directionMarkerZ kalic0re.parkour.tmp += @s kalic0re.parkour.directionMarkerBaseZ
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[0] float 0.0001 run \
    scoreboard players get #directionMarkerX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[1] float 0.0001 run \
    scoreboard players get #directionMarkerY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[2] float 0.0001 run \
    scoreboard players get #directionMarkerZ kalic0re.parkour.tmp
data modify entity @s transformation.translation set from storage kalic0re:parkour tmp.directionMarkerTranslation
