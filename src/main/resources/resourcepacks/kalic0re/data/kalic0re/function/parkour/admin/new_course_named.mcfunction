# /function kalic0re:parkour/admin/new_course_named {course:"<name>"}: New Course with its name filled in.
function kalic0re:parkour/admin/dialog/new_course
$data modify storage kalic0re:dialog built.new_course.inputs[{key:"course_name"}].initial set value "$(course)"
function kalic0re:dialog/show {name:"new_course"}
