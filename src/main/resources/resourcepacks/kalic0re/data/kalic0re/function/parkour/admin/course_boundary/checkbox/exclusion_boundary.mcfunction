# The Remove Boundaries dialog's checkbox for an exclusion boundary, in their orange, unchecked, 1 when checked.
$data modify storage kalic0re:dialog built.remove_boundaries.inputs append value {type:"minecraft:boolean",key:"$(key)", \
    label:{translate:"kalic0re.parkour.menu.remove_boundaries.exclusion_boundary",with:["$(from)","$(to)"],color:"#F9801D"}, \
    initial:false,on_true:"1",on_false:"0"}
