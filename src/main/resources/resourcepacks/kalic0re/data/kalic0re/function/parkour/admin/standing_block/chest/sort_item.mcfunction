# One stack of the chest, in tmp.chest.item. The pane and the campfire in their slots are controls, not entries.
execute if data storage kalic0re:parkour tmp.chest.item{Slot:26b,components:{"minecraft:custom_data":{kalic0re:{standingBlockChest:"mode"}}}} run return 0
execute if data storage kalic0re:parkour tmp.chest.item{Slot:25b,components:{"minecraft:custom_data":{kalic0re:{standingBlockChest:"burning"}}}} run return 0
# Slot is a byte, which a macro would write with its b.
data modify storage kalic0re:parkour tmp.chest.entry set value {}
execute store result storage kalic0re:parkour tmp.chest.entry.slot int 1 run data get storage kalic0re:parkour tmp.chest.item.Slot
# A control anywhere else is a copy, as creative's middle click makes.
execute if data storage kalic0re:parkour tmp.chest.item.components."minecraft:custom_data".kalic0re.standingBlockChest run return run \
    function kalic0re:parkour/admin/standing_block/chest/empty_slot with storage kalic0re:parkour tmp.chest.entry
# A token stands for its entry.
data modify storage kalic0re:parkour tmp.chest.entry.block set from \
    storage kalic0re:parkour tmp.chest.item.components."minecraft:custom_data".kalic0re.standingBlock
execute if data storage kalic0re:parkour tmp.chest.entry.block run return run \
    function kalic0re:parkour/admin/standing_block/chest/token with storage kalic0re:parkour tmp.chest.entry
# Anything else was just put in: a block's own item, or the bucket of a block a runner can be inside.
data modify storage kalic0re:parkour tmp.chest.entry.block set from storage kalic0re:parkour tmp.chest.item.id
function kalic0re:parkour/admin/standing_block/chest/bucket_block with storage kalic0re:parkour tmp.chest.entry
function kalic0re:parkour/admin/standing_block/chest/put_in with storage kalic0re:parkour tmp.chest.entry
