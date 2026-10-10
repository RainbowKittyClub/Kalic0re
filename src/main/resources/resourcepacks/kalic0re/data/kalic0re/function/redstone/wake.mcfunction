# In the tick tag: every 16 ticks, restarts the countdown for a pushed button whose chunk unloaded while it
# was down and is loaded again.
execute if score #16ticks kalic0re.timer matches 0 \
    if entity @e[type=minecraft:marker,tag=kalic0re.redstone.pushedButton,limit=1] run \
    schedule function kalic0re:redstone/button/tick 1t replace
