# As a player with a boundary wand in hand and no outline: once the wand stays 10 ticks, its lore brought up to
# date and its outline shown.
scoreboard players add @s kalic0re.parkour.boundaryWandHandChangeTicks 1
execute if score @s kalic0re.parkour.boundaryWandHandChangeTicks matches 10.. run \
    function kalic0re:parkour/admin/course_boundary/wand/refresh_lore
execute if score @s kalic0re.parkour.boundaryWandHandChangeTicks matches 10.. run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/show
