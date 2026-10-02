# An allowlist forbids standing on anything not listed, and being inside anything enterable not listed
# (enterable_terms_next).
# stepping_on never holds off the ground, so without on_ground every jump would be out.
data modify storage kalic0re:parkour tmp.standingBlockPredicate.allow set value 1b
data modify storage kalic0re:parkour tmp.standingBlockPredicate.forbidden.terms append value {condition:"minecraft:all_of",terms:[ \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_on_ground:true}}},{condition:"minecraft:inverted",term:{}}]}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.forbidden.terms[-1].terms[1].term set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.onListed
