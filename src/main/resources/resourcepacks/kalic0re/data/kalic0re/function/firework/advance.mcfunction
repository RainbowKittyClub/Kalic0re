# Scheduled while any firework is under way, in any world: next tick's run first, so a tick that runs out of commands
# can't strand the rest, then each one's stages due by now; a show that ended 20 ticks back is stale.
execute if entity @e[type=minecraft:marker,tag=kalic0re.firework] run schedule function kalic0re:firework/advance 1t replace
execute store result score #now kalic0re.firework.tmp run time query gametime
scoreboard players operation #staleBefore kalic0re.firework.tmp = #now kalic0re.firework.tmp
scoreboard players remove #staleBefore kalic0re.firework.tmp 20
execute as @e[type=minecraft:marker,tag=kalic0re.firework] at @s run function kalic0re:firework/marker_tick
