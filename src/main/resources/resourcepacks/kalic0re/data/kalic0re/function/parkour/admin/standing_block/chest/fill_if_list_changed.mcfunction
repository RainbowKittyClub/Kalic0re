# The list differs from what the chest last showed: a command changed it, or the chest is new. Refilled.
data modify storage kalic0re:parkour tmp.chest.compare set from storage kalic0re:parkour tmp.chest.stored
execute store success score #standingBlockChestListChanged kalic0re.parkour.tmp run \
    data modify storage kalic0re:parkour tmp.chest.compare set from storage kalic0re:parkour tmp.chest.record.shown
execute if score #standingBlockChestListChanged kalic0re.parkour.tmp matches 1 run function kalic0re:parkour/admin/standing_block/chest/fill
