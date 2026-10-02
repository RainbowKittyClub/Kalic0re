execute unless data storage kalic0re:parkour tmp.chest.fill.entries[0] run return 0
data modify storage kalic0re:parkour tmp.chest.fill.block set from storage kalic0re:parkour tmp.chest.fill.entries[0].block
function kalic0re:parkour/admin/standing_block/chest/put_token with storage kalic0re:parkour tmp.chest.fill
data remove storage kalic0re:parkour tmp.chest.fill.entries[0]
execute store result score #standingBlockChestSlot kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.chest.fill.slot
execute store result storage kalic0re:parkour tmp.chest.fill.slot int 1 run scoreboard players add #standingBlockChestSlot kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/standing_block/chest/fill_next
