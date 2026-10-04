# More than one boundary holds this player's feet: a line each, its link remove_answer with 0 for the first, 1
# for the next and so on. The question replaces any earlier one under their UUID, so each link removes the
# boundary it showed.
data modify storage kalic0re:parkour tmp.boundaryHere.question set value {}
data modify storage kalic0re:parkour tmp.boundaryHere.question.uuid set from entity @s UUID
data modify storage kalic0re:parkour tmp.boundaryHere.question.choices set from storage kalic0re:parkour tmp.boundaryHere.found
function kalic0re:parkour/admin/course_boundary/forget_question with storage kalic0re:parkour tmp.boundaryHere.question
data modify storage kalic0re:parkour courseBoundaryRemovalQuestions append from storage kalic0re:parkour tmp.boundaryHere.question
function kalic0re:parkour/admin/course_boundary/message/which with storage kalic0re:parkour tmp.boundaryHere
scoreboard players set #boundaryAnswer kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/course_boundary/ask_which_next
