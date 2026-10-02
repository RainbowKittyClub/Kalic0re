# A token for $(block) in the first empty slot from $(slot); none if the 26 are full, so the next tick
# finds it missing and drops the entry.
$execute unless items entity @s container.$(slot) * run return run \
    function kalic0re:parkour/admin/standing_block/chest/put_token {slot:$(slot),block:"$(block)"}
$scoreboard players set #standingBlockChestSlot kalic0re.parkour.tmp $(slot)
execute if score #standingBlockChestSlot kalic0re.parkour.tmp matches 25.. run return 0
execute store result storage kalic0re:parkour tmp.chest.place.slot int 1 run scoreboard players add #standingBlockChestSlot kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/standing_block/chest/place_token with storage kalic0re:parkour tmp.chest.place
