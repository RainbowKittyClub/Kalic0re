# add_boundary's report, from tmp.newCourseBoundary.
$tellraw @s {translate:"kalic0re.parkour.chat.course_boundary_added",fallback:"Parkour course \"%s\": course boundary %s added, from %s to %s.", \
    with:["$(course)",{nbt:"tmp.newCourseBoundary.number",storage:"kalic0re:parkour"}, \
    {nbt:"tmp.newCourseBoundary.boundary.min[]",storage:"kalic0re:parkour",separator:" "}, \
    {nbt:"tmp.newCourseBoundary.boundary.max[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
