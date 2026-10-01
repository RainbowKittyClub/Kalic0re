# As a player whose outline shows with no boundary wand in hand: hides it once the wand stays out 10 ticks.
scoreboard players add @s kalic0re.parkour.boundaryWandHandChangeTicks 1
execute if score @s kalic0re.parkour.boundaryWandHandChangeTicks matches 10.. run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/hide
