# Block ids share one term and tags get one each: a predicate's block set is a list of ids or one tag.
# Blocks a runner can be inside (enterableStandingBlocks) are also tested at their feet, by their inTerm.
data modify storage kalic0re:parkour tmp.standingBlockPredicate set value \
    {ids:[],onListed:{condition:"minecraft:any_of",terms:[]},forbidden:{condition:"minecraft:any_of",terms:[]}}
data modify storage kalic0re:parkour tmp.standingBlockPredicate.enterables set from storage kalic0re:parkour enterableStandingBlocks
$data modify storage kalic0re:parkour tmp.standingBlockPredicate.entries set from storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks
function kalic0re:parkour/admin/standing_block/sort_next_entry
execute if data storage kalic0re:parkour tmp.standingBlockPredicate.ids[0] run function kalic0re:parkour/admin/standing_block/append_ids_term
$execute if data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run \
    function kalic0re:parkour/admin/standing_block/forbid_unlisted
$execute unless data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run \
    data modify storage kalic0re:parkour tmp.standingBlockPredicate.forbidden.terms set from storage kalic0re:parkour tmp.standingBlockPredicate.onListed.terms
function kalic0re:parkour/admin/standing_block/enterable_terms_next
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicate set from \
    storage kalic0re:parkour tmp.standingBlockPredicate.forbidden
data remove storage kalic0re:parkour tmp.standingBlockPredicate
