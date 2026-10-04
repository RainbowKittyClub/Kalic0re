# For move_point: $(position) facing $(rotation) into tmp.pos, by a marker stood there. Alone in a function of its
# own: text that isn't a place fails to fill only this, and leaves tmp.pos unset.
$execute positioned $(position) rotated $(rotation) summon minecraft:marker run function kalic0re:parkour/checkpoint/capture_marker
