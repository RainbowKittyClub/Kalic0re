# Enter until both steps are saved; each save skips itself once its copy exists.
execute unless entity @s[tag=kalic0re.parkour.playerInventorySaved, \
    tag=kalic0re.parkour.playerEffectsSaved] \
    run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/enter"}
# enter refuses a run with no course and clears isActive.
execute unless score @s kalic0re.parkour.isActive matches 1 run return 0
scoreboard players add @s kalic0re.parkour.runTicks 1
function kalic0re:parkour/player/refill
execute if score @s kalic0re.parkour.click matches 1.. run function kalic0re:parkour/player/click
# Feet outside all of the course's inclusion zones go back to the checkpoint.
execute store result storage kalic0re:parkour tmp.inclusionZoneCheck.id int 1 run \
    scoreboard players get @s kalic0re.parkour.runningCourseId
function kalic0re:parkour/inclusion_zone/check with storage kalic0re:parkour tmp.inclusionZoneCheck
data remove storage kalic0re:parkour tmp.inclusionZoneCheck
# Parkour items only exist on active players, so their drops are always near one.
execute as @e[type=item,distance=..8] if items entity @s contents *[custom_data~{kalic0re:{parkour:{}}}] run kill @s
# After the clicks, so a restart shows its reset this same tick.
function kalic0re:parkour/timer/show
