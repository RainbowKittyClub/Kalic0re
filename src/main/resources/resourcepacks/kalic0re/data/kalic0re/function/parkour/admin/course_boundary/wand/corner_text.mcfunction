# tmp.wand.$(corner)'s block as the dialog's text for it, "x y z", at kalic0re:dialog args.$(corner).
$data modify storage kalic0re:parkour tmp.wand.cornerText set value {corner:"$(corner)"}
$data modify storage kalic0re:parkour tmp.wand.cornerText.x set from storage kalic0re:parkour tmp.wand.$(corner)[0]
$data modify storage kalic0re:parkour tmp.wand.cornerText.y set from storage kalic0re:parkour tmp.wand.$(corner)[1]
$data modify storage kalic0re:parkour tmp.wand.cornerText.z set from storage kalic0re:parkour tmp.wand.$(corner)[2]
function kalic0re:parkour/admin/course_boundary/wand/corner_text_into_args with storage kalic0re:parkour tmp.wand.cornerText
