# As a standing-blocks chest: gone with its seat and record. Emptied first, as a killed chest minecart
# spills its contents.
data modify entity @s Items set value []
execute store result storage kalic0re:parkour tmp.standingBlockChestClose.courseId int 1 run \
    scoreboard players get @s kalic0re.parkour.standingBlockChestCourseId
function kalic0re:parkour/admin/standing_block/chest/forget with storage kalic0re:parkour tmp.standingBlockChestClose
data remove storage kalic0re:parkour tmp.standingBlockChestClose
execute on vehicle run kill @s
kill @s
