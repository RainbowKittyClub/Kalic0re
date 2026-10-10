# /function kalic0re:redstone/push_button_for {position:"<x y z>",ticks:<n>}
# e.g. {position:"10 64 10",ticks:100}
# Pushes the button at position and holds it down for ticks, then lets it up. Whatever it powers, through
# the block it's on too, turns on and off with it, and it clicks each way. Coordinates read as in a
# /setblock of your own. Returns ticks, and fails, changing nothing, on ticks under 1, a position not
# loaded, or anything but an unpressed button vanilla has. Reading the button each tick keeps its chunk
# loaded until it's let up; across a restart, the wait carries on once the chunk loads again, up to 16
# ticks late. A sculk sensor hears neither click: a command can't make a press's vibration.
scoreboard players set #buttonPushTicks kalic0re.redstone.tmp 0
$scoreboard players set #buttonPushTicks kalic0re.redstone.tmp $(ticks)
execute unless score #buttonPushTicks kalic0re.redstone.tmp matches 1.. run return fail
$return run function kalic0re:redstone/button/push {position:"$(position)"}
