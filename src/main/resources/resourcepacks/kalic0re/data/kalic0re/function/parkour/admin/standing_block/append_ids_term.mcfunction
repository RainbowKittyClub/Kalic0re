# On the listed ids: the block holding the runner up, or, on the ground, one at their feet. A block with
# nothing to stand on, as one snow layer or a pressure plate, leaves the block below holding them up.
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms append value \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{stepping_on:{block:{blocks:[]}}}}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms[-1].predicate.stepping_on.block.blocks set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.ids
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms append value {condition:"minecraft:all_of",terms:[ \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_on_ground:true}}}, \
    {condition:"minecraft:location_check",predicate:{block:{blocks:[]}}}]}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms[-1].terms[1].predicate.block.blocks set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.ids
