# Run as a player, e.g. from a pressure plate's command block, to make where they stand their checkpoint.
execute unless score @s kalic0re.parkour.isActive matches 1 run return fail
function kalic0re:parkour/pos/capture
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/checkpoint/store"}
