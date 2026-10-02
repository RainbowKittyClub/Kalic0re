execute unless score @s kalic0re.parkour.boundaryOutlineNextBoundary matches 0.. run return 0
# Course boundaries in white, then exclusion boundaries in orange.
data modify storage kalic0re:parkour tmp.outline set value \
    {next:{list:"courseBoundaries",colors:{pane:"white_stained_glass_pane",glow:16777215}}}
execute if entity @s[tag=kalic0re.parkour.boundaryOutlineDrawingExclusionBoundaries] run data modify storage kalic0re:parkour \
    tmp.outline.next set value {list:"courseExclusionBoundaries",colors:{pane:"orange_stained_glass_pane",glow:16351261}}
execute store result storage kalic0re:parkour tmp.outline.next.id int 1 run \
    scoreboard players get @s kalic0re.parkour.boundaryOutlineCourseId
execute store result storage kalic0re:parkour tmp.outline.next.index int 1 run \
    scoreboard players get @s kalic0re.parkour.boundaryOutlineNextBoundary
function kalic0re:parkour/admin/course_boundary/wand/outline/draw_boundary_at with storage kalic0re:parkour tmp.outline.next
data remove storage kalic0re:parkour tmp.outline
