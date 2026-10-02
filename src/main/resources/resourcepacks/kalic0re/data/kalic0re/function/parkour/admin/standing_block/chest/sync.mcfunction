# As a standing-blocks chest whose owner is near, tagged standingBlockChestEditor.
data modify storage kalic0re:parkour tmp.chest set value {}
execute store result storage kalic0re:parkour tmp.chest.courseId int 1 run scoreboard players get @s kalic0re.parkour.standingBlockChestCourseId
# A deleted course, or a chest from before the last reload, has no record: it closes.
execute store result score #standingBlockChestCourseFound kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/standing_block/chest/read_course with storage kalic0re:parkour tmp.chest
execute if score #standingBlockChestCourseFound kalic0re.parkour.tmp matches 0 run return run \
    function kalic0re:parkour/admin/standing_block/chest/close
# A token taken out is gone: it stands for an entry in the list, not a block anyone owns.
clear @a[distance=..8] *[custom_data~{kalic0re:{standingBlockChestItem:1b}}]
execute as @e[type=minecraft:item,distance=..8] if items entity @s contents *[custom_data~{kalic0re:{standingBlockChestItem:1b}}] run kill @s
# The pane gone from its slot, once shown there, switches the mode.
scoreboard players set #standingBlockChestToggle kalic0re.parkour.tmp 0
execute if data storage kalic0re:parkour tmp.chest.record.paneShown \
    unless items entity @s container.26 *[custom_data~{kalic0re:{standingBlockChest:"mode"}}] run \
    scoreboard players set #standingBlockChestToggle kalic0re.parkour.tmp 1
data modify storage kalic0re:parkour tmp.chest.entries set value []
data modify storage kalic0re:parkour tmp.chest.items set from entity @s Items
function kalic0re:parkour/admin/standing_block/chest/next_item
# Changed in the chest: to the list. Otherwise, changed in the list by a command: to the chest.
data modify storage kalic0re:parkour tmp.chest.compare set from storage kalic0re:parkour tmp.chest.entries
execute store success score #standingBlockChestChanged kalic0re.parkour.tmp run \
    data modify storage kalic0re:parkour tmp.chest.compare set from storage kalic0re:parkour tmp.chest.record.shown
execute if score #standingBlockChestChanged kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/standing_block/chest/save_entries with storage kalic0re:parkour tmp.chest
execute if score #standingBlockChestChanged kalic0re.parkour.tmp matches 0 run \
    function kalic0re:parkour/admin/standing_block/chest/fill_if_list_changed
execute if score #standingBlockChestToggle kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/standing_block/chest/toggle with storage kalic0re:parkour tmp.chest
function kalic0re:parkour/admin/standing_block/chest/pane
function kalic0re:parkour/admin/standing_block/chest/save_record with storage kalic0re:parkour tmp.chest
