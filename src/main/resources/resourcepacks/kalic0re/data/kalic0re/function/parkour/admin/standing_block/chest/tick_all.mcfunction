# While any standing-blocks chest is recorded: each kept in step with its list.
execute as @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest] at @s run \
    function kalic0re:parkour/admin/standing_block/chest/tick
# A chest broken in creative leaves its seat and record behind.
execute as @e[type=minecraft:block_display,tag=kalic0re.parkour.standingBlockChestSeat] at @s \
    unless entity @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest,distance=..1] run kill @s
execute unless entity @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest,limit=1] run \
    data remove storage kalic0re:parkour standingBlockChests
