# The Remove Boundaries dialog's checkbox for a course boundary, unchecked, 1 when checked.
$data modify storage kalic0re:dialog built.remove_boundaries.inputs append value {type:"minecraft:boolean",key:"$(key)", \
    label:{translate:"kalic0re.parkour.menu.remove_boundaries.course_boundary",with:["$(from)","$(to)"]}, \
    initial:false,on_true:"1",on_false:"0"}
