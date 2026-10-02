# As a standing-blocks chest, at it, every tick: kept while its owner is within 8 blocks, and its contents
# and the course's list kept in step, each way.
scoreboard players operation #standingBlockChestOwner kalic0re.parkour.tmp = @s kalic0re.parkour.standingBlockChestOwner
execute as @a[distance=..8] if score @s kalic0re.parkour.standingBlockChestOwner = #standingBlockChestOwner kalic0re.parkour.tmp run \
    tag @s add kalic0re.parkour.standingBlockChestEditor
execute if entity @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/standing_block/chest/sync
execute unless entity @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/standing_block/chest/close
tag @a[tag=kalic0re.parkour.standingBlockChestEditor] remove kalic0re.parkour.standingBlockChestEditor
data remove storage kalic0re:parkour tmp.chest
