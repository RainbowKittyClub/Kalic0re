# For add_from_dialog: 1 when tmp.dialogTexts.$(corner) is coordinates alone and reads as a position, as positioned
# reads one (~ from you), its block then at tmp.boundaryDialog.$(corner).
data modify storage kalic0re:parkour tmp.textCheck set value {rest:""}
$data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.$(corner)
execute unless function kalic0re:parkour/admin/text/only_coordinate_characters run return fail
data remove storage kalic0re:parkour tmp.textCheck
$data modify storage kalic0re:parkour tmp.cornerText set value {corner:"$(corner)",text:""}
$data modify storage kalic0re:parkour tmp.cornerText.text set from storage kalic0re:parkour tmp.dialogTexts.$(corner)
function kalic0re:parkour/admin/course_boundary/read_corner_block with storage kalic0re:parkour tmp.cornerText
data remove storage kalic0re:parkour tmp.cornerText
$execute if data storage kalic0re:parkour tmp.boundaryDialog.$(corner) run return 1
return fail
