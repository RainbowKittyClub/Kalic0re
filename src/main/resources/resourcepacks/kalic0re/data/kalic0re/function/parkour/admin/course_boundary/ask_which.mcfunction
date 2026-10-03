# More than one boundary holds this player's feet: a line each, its link the trigger set to 2 for the first,
# 3 for the next and so on. The question replaces any earlier one under their UUID, so each link removes the
# boundary it showed. The trigger is enabled here too, for an op who called remove_here as a function.
scoreboard players enable @s kalic0re.admin.remove_course_boundary
data modify storage kalic0re:parkour tmp.boundaryHere.question set value {}
data modify storage kalic0re:parkour tmp.boundaryHere.question.uuid set from entity @s UUID
data modify storage kalic0re:parkour tmp.boundaryHere.question.choices set from storage kalic0re:parkour tmp.boundaryHere.found
function kalic0re:parkour/admin/course_boundary/forget_question with storage kalic0re:parkour tmp.boundaryHere.question
data modify storage kalic0re:parkour courseBoundaryRemovalQuestions append from storage kalic0re:parkour tmp.boundaryHere.question
function kalic0re:parkour/admin/course_boundary/message/which with storage kalic0re:parkour tmp.boundaryHere
scoreboard players set #boundaryAnswer kalic0re.parkour.tmp 2
function kalic0re:parkour/admin/course_boundary/ask_which_next
