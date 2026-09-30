# $(course)'s management dialog. Its Back opens the Courses dialog embedded here, running no command.
function kalic0re:parkour/admin/dialog/build_courses
$data modify storage kalic0re:dialog args set value {course:"$(course)"}
function kalic0re:parkour/admin/dialog/manage
data modify storage kalic0re:dialog built.manage.exit_action.action.dialog set from storage kalic0re:dialog built.courses
