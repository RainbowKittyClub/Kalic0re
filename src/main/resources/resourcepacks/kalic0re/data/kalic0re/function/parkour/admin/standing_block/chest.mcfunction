# /function kalic0re:parkour/admin/standing_block/chest {course:"<name>"}
# A chest minecart at eye level holding the course's standing blocks, one token each, the burning campfire and
# the mode's pane. chest/tick keeps it and the course in step until its owner walks away.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
# 25 slots for blocks; the last two hold the campfire and the pane.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[25] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.standing_blocks_too_many",with:["$(course)"],color:"red"}
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$execute store result score #standingBlockChestCourseId kalic0re.parkour.tmp run data get storage kalic0re:parkour courses[{name:"$(course)"}].id
execute unless score @s kalic0re.parkour.standingBlockChestOwner matches 1.. run \
    function kalic0re:parkour/admin/standing_block/chest/assign_owner
# One chest per course and one per owner: an older one goes.
scoreboard players operation #standingBlockChestOwner kalic0re.parkour.tmp = @s kalic0re.parkour.standingBlockChestOwner
execute as @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest] \
    if score @s kalic0re.parkour.standingBlockChestCourseId = #standingBlockChestCourseId kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/standing_block/chest/close
execute as @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest] \
    if score @s kalic0re.parkour.standingBlockChestOwner = #standingBlockChestOwner kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/standing_block/chest/close
# Riding an invisible display, so nothing pushes it; its middle at eye level, a step ahead.
$execute anchored eyes positioned ^ ^ ^1.5 positioned ~ ~-0.35 ~ run summon minecraft:block_display ~ ~ ~ \
    {Tags:["kalic0re.parkour.standingBlockChestSeat"],Passengers:[{id:"minecraft:chest_minecart",NoGravity:1b,Invulnerable:1b,Silent:1b, \
    CustomName:{translate:"kalic0re.parkour.item.standing_block_chest",with:["$(course)"]}, \
    Tags:["kalic0re.parkour.standingBlockChest","kalic0re.parkour.standingBlockChestNew"]}]}
execute as @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChestNew] run \
    function kalic0re:parkour/admin/standing_block/chest/claim
# Shown nothing yet, so its first tick fills it from the list.
data modify storage kalic0re:parkour tmp.standingBlockChestRecord set value {shown:[]}
execute store result storage kalic0re:parkour tmp.standingBlockChestRecord.courseId int 1 run \
    scoreboard players get #standingBlockChestCourseId kalic0re.parkour.tmp
function kalic0re:parkour/admin/standing_block/chest/forget with storage kalic0re:parkour tmp.standingBlockChestRecord
data modify storage kalic0re:parkour standingBlockChests append from storage kalic0re:parkour tmp.standingBlockChestRecord
data remove storage kalic0re:parkour tmp.standingBlockChestRecord
$tellraw @s {translate:"kalic0re.parkour.chat.standing_block_chest",with:["$(course)"],color:"green"}
