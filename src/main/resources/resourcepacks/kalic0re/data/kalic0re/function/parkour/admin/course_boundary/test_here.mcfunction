# Keeps the boundary in tmp.boundaryHere.scan when its term holds this player's feet.
$execute if predicate $(term) run \
    data modify storage kalic0re:parkour tmp.boundaryHere.found append from storage kalic0re:parkour tmp.boundaryHere.scan
