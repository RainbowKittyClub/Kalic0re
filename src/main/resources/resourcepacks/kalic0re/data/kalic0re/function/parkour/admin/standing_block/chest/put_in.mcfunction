# A stack of $(block) just put in slot $(slot): back to its owner, at their feet and picked up at once,
# with a token in its place if it's a block the list hasn't got.
execute at @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/standing_block/chest/give_back with storage kalic0re:parkour tmp.chest
$item replace entity @s container.$(slot) with minecraft:air
execute store success score #standingBlockChestIsBlock kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/standing_block/chest/is_block with storage kalic0re:parkour tmp.chest.entry
$execute if score #standingBlockChestIsBlock kalic0re.parkour.tmp matches 0 run return run \
    tellraw @a[tag=kalic0re.parkour.standingBlockChestEditor] {translate:"kalic0re.parkour.chat.standing_block_not_a_block",with:["$(block)"],color:"red"}
$execute if data storage kalic0re:parkour tmp.chest.entries[{block:"$(block)"}] run return 0
$data modify storage kalic0re:parkour tmp.chest.entries append value {block:"$(block)"}
$data modify storage kalic0re:parkour tmp.chest.place set value {slot:0,block:"$(block)"}
function kalic0re:parkour/admin/standing_block/chest/place_token with storage kalic0re:parkour tmp.chest.place
