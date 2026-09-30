# Gives tmp.give ({id, duration, amplifier, show_particles}) to @s.
execute if data storage kalic0re:parkour tmp.give{show_particles:0b} run \
    data modify storage kalic0re:parkour tmp.give.hide set value "true"
# Amplifiers save as signed bytes: 128-255 (jump_boost 128+ blocks jumping) read back negative.
execute store result score #amplifier kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.give.amplifier
execute if score #amplifier kalic0re.parkour.tmp matches ..-1 run \
    scoreboard players add #amplifier kalic0re.parkour.tmp 256
execute store result storage kalic0re:parkour tmp.give.amplifier int 1 run \
    scoreboard players get #amplifier kalic0re.parkour.tmp
execute if data storage kalic0re:parkour tmp.give{duration:-1} run return run \
    function kalic0re:parkour/effects/give_infinite with storage kalic0re:parkour tmp.give
# /effect give takes whole seconds; ticks x 0.05 floors, and it rejects the 0 left under one second.
execute store result storage kalic0re:parkour tmp.give.seconds int 0.05 run \
    data get storage kalic0re:parkour tmp.give.duration
execute if data storage kalic0re:parkour tmp.give{seconds:0} run return 0
function kalic0re:parkour/effects/give_timed with storage kalic0re:parkour tmp.give
