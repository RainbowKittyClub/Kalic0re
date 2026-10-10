# Scheduled while any pushed button is held down: each one's countdown, then again next tick. Idle, it
# costs nothing, as nothing is scheduled; redstone/wake restarts it for one whose chunk loads again.
execute as @e[type=minecraft:marker,tag=kalic0re.redstone.pushedButton] at @s run \
    function kalic0re:redstone/button/count_down
execute if entity @e[type=minecraft:marker,tag=kalic0re.redstone.pushedButton,limit=1] run \
    schedule function kalic0re:redstone/button/tick 1t replace
