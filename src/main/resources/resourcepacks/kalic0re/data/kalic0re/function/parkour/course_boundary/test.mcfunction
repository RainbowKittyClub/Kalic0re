# any_of stops at the first rule the runner breaks; the course boundaries' own, inverted, at the first
# that holds them. Cached by argument, it parses once.
$execute if predicate $(runnerOutOfBoundsPredicateText) run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
