# /function kalic0re:parkour/admin/course_boundary/remove_answer {index:<n>}, as remove_here's question links run
# it: choice $(index), from 0, of the last question this player was asked, kept under their UUID.
data modify storage kalic0re:parkour tmp.courseBoundaryAnswer set value {}
data modify storage kalic0re:parkour tmp.courseBoundaryAnswer.uuid set from entity @s UUID
$data modify storage kalic0re:parkour tmp.courseBoundaryAnswer.index set value $(index)
function kalic0re:parkour/admin/course_boundary/remove_answer_of with storage kalic0re:parkour tmp.courseBoundaryAnswer
data remove storage kalic0re:parkour tmp.courseBoundaryAnswer
