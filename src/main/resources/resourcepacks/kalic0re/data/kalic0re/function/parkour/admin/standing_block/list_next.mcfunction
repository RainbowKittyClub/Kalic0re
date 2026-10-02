execute unless data storage kalic0re:parkour tmp.standingBlockList.blocks[0] run return 0
data modify storage kalic0re:parkour tmp.standingBlockList.block set from storage kalic0re:parkour tmp.standingBlockList.blocks[0].block
function kalic0re:parkour/admin/standing_block/list_line with storage kalic0re:parkour tmp.standingBlockList
data remove storage kalic0re:parkour tmp.standingBlockList.blocks[0]
function kalic0re:parkour/admin/standing_block/list_next
