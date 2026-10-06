# New Course again, every box holding what was typed into it (tmp.dialogTexts), for a refusal to answer.
function kalic0re:parkour/admin/dialog/new_course
data modify storage kalic0re:dialog built.new_course.inputs[{key:"course_name"}].initial set from storage kalic0re:parkour tmp.dialogTexts.course
data modify storage kalic0re:dialog built.new_course.inputs[{key:"start_position"}].initial set from storage kalic0re:parkour tmp.dialogTexts.startPosition
data modify storage kalic0re:dialog built.new_course.inputs[{key:"start_rotation"}].initial set from storage kalic0re:parkour tmp.dialogTexts.startRotation
data modify storage kalic0re:dialog built.new_course.inputs[{key:"lobby_position"}].initial set from storage kalic0re:parkour tmp.dialogTexts.lobbyPosition
data modify storage kalic0re:dialog built.new_course.inputs[{key:"lobby_rotation"}].initial set from storage kalic0re:parkour tmp.dialogTexts.lobbyRotation
function kalic0re:dialog/show {name:"new_course"}
