data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms append value \
    {condition:"minecraft:entity_properties",entity:"this",predicate:{stepping_on:{block:{blocks:""}}}}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms[-1].predicate.stepping_on.block.blocks set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.entries[0].block
