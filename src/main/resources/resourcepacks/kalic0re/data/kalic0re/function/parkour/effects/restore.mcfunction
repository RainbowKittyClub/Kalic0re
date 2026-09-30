# No saved list (a run begun before effects were saved) leaves the player's effects alone.
$execute unless data storage kalic0re:parkour players[{uuid:$(uuid)}].effects run return 0
effect clear @s
$data modify storage kalic0re:parkour tmp.effects set from storage kalic0re:parkour players[{uuid:$(uuid)}].effects
function kalic0re:parkour/effects/restore_next
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].effects
data remove storage kalic0re:parkour tmp.effects
