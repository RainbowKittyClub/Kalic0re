data modify storage kalic0re:parkour tmp.standingBlockPredicate.first set string storage kalic0re:parkour tmp.standingBlockPredicate.entries[0].block 0 1
execute if data storage kalic0re:parkour tmp.standingBlockPredicate{first:"#"} run function kalic0re:parkour/admin/standing_block/append_tag_term
execute unless data storage kalic0re:parkour tmp.standingBlockPredicate{first:"#"} run \
    data modify storage kalic0re:parkour tmp.standingBlockPredicate.ids append from storage kalic0re:parkour tmp.standingBlockPredicate.entries[0].block
