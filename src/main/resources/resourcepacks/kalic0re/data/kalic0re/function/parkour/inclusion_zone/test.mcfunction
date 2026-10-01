# any_of stops at the first zone holding the runner; macros cache it by argument, so it's parsed once.
# Feet outside every zone go where the checkpoint item would, the timer running on.
$execute unless predicate $(inclusionZonePredicate) run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
