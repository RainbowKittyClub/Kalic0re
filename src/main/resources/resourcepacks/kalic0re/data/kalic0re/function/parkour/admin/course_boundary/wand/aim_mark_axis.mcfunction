# A display's local x, y and z are its facing's left, up and forward, so ^ stands the marker along the axis as drawn.
$execute positioned ^$(x) ^$(y) ^$(z) summon minecraft:marker run \
    function kalic0re:parkour/admin/course_boundary/wand/aim_read_axis {axis:"$(axis)"}
