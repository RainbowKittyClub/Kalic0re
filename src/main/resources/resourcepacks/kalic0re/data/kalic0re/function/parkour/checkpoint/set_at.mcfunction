# Run as a player, positioned and facing where their checkpoint should be, e.g. from a command block:
#   execute positioned 85 100 -31 as @p[distance=..2] facing 91 102 -31 run function kalic0re:parkour/checkpoint/set_at
# A whole-number x or z is the block's centre, as positioned takes it. The position can't be read from the
# execute context, only used, so a marker summoned there carries it; the dimension is the player's.
execute unless entity @s[type=minecraft:player] run return fail
execute unless score @s kalic0re.parkour.isActive matches 1 run return fail
execute summon minecraft:marker run function kalic0re:parkour/checkpoint/capture_marker
data modify storage kalic0re:parkour tmp.pos.dim set from entity @s Dimension
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/checkpoint/store"}
