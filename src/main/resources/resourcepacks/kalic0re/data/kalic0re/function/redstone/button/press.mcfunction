# At an unpressed button's centre: the button down, with its click, and a marker left to let it up after
# #buttonPushTicks. Fails, changing nothing, on a button vanilla lacks.
execute unless function kalic0re:redstone/button/read run return fail
data modify storage kalic0re:redstone button merge value {powered:"true",click:"on"}
function kalic0re:redstone/button/set with storage kalic0re:redstone button
data remove storage kalic0re:redstone button
execute summon minecraft:marker run function kalic0re:redstone/button/hold
schedule function kalic0re:redstone/button/tick 1t replace
return run scoreboard players get #buttonPushTicks kalic0re.redstone.tmp
