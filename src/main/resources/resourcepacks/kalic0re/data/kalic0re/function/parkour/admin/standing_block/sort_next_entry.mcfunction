execute unless data storage kalic0re:parkour tmp.standingBlockPredicate.entries[0] run return 0
execute store result score #standingBlockOnlyEntered kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/standing_block/mark_enterable with storage kalic0re:parkour tmp.standingBlockPredicate.entries[0]
execute if score #standingBlockOnlyEntered kalic0re.parkour.tmp matches 0 run function kalic0re:parkour/admin/standing_block/sort_block_entry
data remove storage kalic0re:parkour tmp.standingBlockPredicate.entries[0]
function kalic0re:parkour/admin/standing_block/sort_next_entry
