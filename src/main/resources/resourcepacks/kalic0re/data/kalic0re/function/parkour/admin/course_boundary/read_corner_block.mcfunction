# For read_corner_text: alone in a function of its own, so text that isn't a position fails only this.
$execute positioned $(text) align xyz summon minecraft:marker run \
    function kalic0re:parkour/admin/read_block_of_marker {into:"boundaryDialog.$(corner)"}
