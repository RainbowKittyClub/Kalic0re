# Feet inside a block a runner can be in: out for each listed one on a denylist, each unlisted one on an allowlist.
execute unless data storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0] run return 0
execute unless data storage kalic0re:parkour tmp.standingBlockPredicate.allow if data storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0].listed run \
    data modify storage kalic0re:parkour tmp.standingBlockPredicate.forbidden.terms append from storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0].inTerm
execute if data storage kalic0re:parkour tmp.standingBlockPredicate.allow unless data storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0].listed run \
    data modify storage kalic0re:parkour tmp.standingBlockPredicate.forbidden.terms append from storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0].inTerm
data remove storage kalic0re:parkour tmp.standingBlockPredicate.enterables[0]
function kalic0re:parkour/admin/standing_block/enterable_terms_next
