# As ask_which, but in the Remove Boundaries dialog: a checkbox per boundary in tmp.boundaryHere.found, exclusion
# boundaries orange, and Remove removing the checked ones. The choices are kept under their UUID as ask_which's are.
data modify storage kalic0re:parkour tmp.boundaryHere.question set value {}
data modify storage kalic0re:parkour tmp.boundaryHere.question.uuid set from entity @s UUID
data modify storage kalic0re:parkour tmp.boundaryHere.question.choices set from storage kalic0re:parkour tmp.boundaryHere.found
function kalic0re:parkour/admin/course_boundary/forget_question with storage kalic0re:parkour tmp.boundaryHere.question
data modify storage kalic0re:parkour courseBoundaryRemovalQuestions append from storage kalic0re:parkour tmp.boundaryHere.question
data modify storage kalic0re:dialog args set value {}
data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour tmp.boundaryHere.course
data modify storage kalic0re:dialog args.dim set from entity @s Dimension
function kalic0re:parkour/admin/dialog/remove_boundaries
# Remove's template names each checkbox's key, $(c0),$(c1)…; a literal $( can't be written by a macro line, so
# it comes in as an argument.
data modify storage kalic0re:parkour tmp.boundaryHere.line set value {index:0,checks:"",separator:"",dollar:"$"}
function kalic0re:parkour/admin/course_boundary/ask_which_by_dialog_next
function kalic0re:parkour/admin/course_boundary/ask_which_by_dialog_template with storage kalic0re:parkour tmp.boundaryHere.line
function kalic0re:dialog/show {name:"remove_boundaries"}
