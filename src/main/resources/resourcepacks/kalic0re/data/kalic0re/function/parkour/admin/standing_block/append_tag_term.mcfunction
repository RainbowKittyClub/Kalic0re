# As append_ids_term, for the tag in entries[0]: the block holding the runner up, or one at their feet.
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms append value \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{stepping_on:{block:{blocks:""}}}}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms[-1].predicate.stepping_on.block.blocks set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.entries[0].block
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms append value {condition:"minecraft:all_of",terms:[ \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_on_ground:true}}}, \
    {condition:"minecraft:location_check",predicate:{block:{blocks:""}}}]}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms[-1].terms[1].predicate.block.blocks set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.entries[0].block
