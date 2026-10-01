# any_of stops at the first boundary that holds the runner. Cached by argument, it parses once.
# Feet outside every boundary go where the checkpoint item would, the timer running on.
$execute unless predicate $(courseBoundaryPredicate) run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
