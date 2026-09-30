# Adds a course_button.json to the Courses dialog for each name left in tmp.names.
execute unless data storage kalic0re:parkour tmp.names[0] run return 0
data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour tmp.names[0]
function kalic0re:parkour/admin/dialog/course_button
data modify storage kalic0re:dialog built.courses.actions append from storage kalic0re:dialog built.course_button
data remove storage kalic0re:parkour tmp.names[0]
function kalic0re:parkour/admin/dialog/add_course_buttons
