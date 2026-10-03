# Run as a player, e.g. from a pressure plate's command block, to make where they stand their checkpoint.
# set_at takes a position and facing instead. Without a player as @s, e.g. after `at` rather than `as`,
# it refuses: a missing @s fails the score test rather than returning.
execute unless entity @s[type=minecraft:player] run return fail
execute unless score @s kalic0re.parkour.isActive matches 1 run return fail
function kalic0re:parkour/pos/capture
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/checkpoint/store"}
