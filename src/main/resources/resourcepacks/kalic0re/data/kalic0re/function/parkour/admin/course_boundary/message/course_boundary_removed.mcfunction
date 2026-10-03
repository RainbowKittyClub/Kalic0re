# remove_at's report, from tmp.boundaryRemoval.
$tellraw @s {translate:"kalic0re.parkour.chat.course_boundary_removed",with:["$(course)", \
    {nbt:"tmp.boundaryRemoval.boundary.min[]",storage:"kalic0re:parkour",separator:" "}, \
    {nbt:"tmp.boundaryRemoval.boundary.max[]",storage:"kalic0re:parkour",separator:" "},"$(dim)"],color:"green"}
