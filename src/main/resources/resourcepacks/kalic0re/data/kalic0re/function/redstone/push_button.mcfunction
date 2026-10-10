# /function kalic0re:redstone/push_button {position:"<x y z>"}
# e.g. {position:"~ ~1 ~2"}
# Pushes the button at position for as long as a player's push holds it down: 30 ticks (1.5 seconds) a
# wooden button, 20 (1 second) a stone or polished blackstone one. Otherwise as push_button_for.
$execute positioned $(position) unless loaded ~ ~ ~ run return fail
scoreboard players set #buttonPushTicks kalic0re.redstone.tmp 30
$execute positioned $(position) if block ~ ~ ~ #minecraft:stone_buttons run \
    scoreboard players set #buttonPushTicks kalic0re.redstone.tmp 20
$return run function kalic0re:redstone/button/push {position:"$(position)"}
