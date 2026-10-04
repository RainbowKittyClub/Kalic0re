# The block $(text) is in, as `positioned` reads it, at tmp.boundaryDialog.$(corner). Alone in a function of its
# own: text that's no position fails to fill only this, and leaves the corner unset.
data remove storage kalic0re:parkour tmp.wand.pos
$execute positioned $(text) align xyz summon minecraft:marker run function kalic0re:parkour/admin/read_block_of_marker {into:"wand.pos"}
$data modify storage kalic0re:parkour tmp.boundaryDialog.$(corner) set from storage kalic0re:parkour tmp.wand.pos
data remove storage kalic0re:parkour tmp.wand
