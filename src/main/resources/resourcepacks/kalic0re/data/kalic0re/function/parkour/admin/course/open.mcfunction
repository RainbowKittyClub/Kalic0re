# A management dialog button: opens admin/dialog/$(dialog) for $(course), unless renamed or deleted meanwhile.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
# Back opens the course's management dialog embedded here, running no command.
$function kalic0re:parkour/admin/dialog/build_manage {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/$(dialog)
$data modify storage kalic0re:dialog built.$(dialog).no.action.dialog set from storage kalic0re:dialog built.manage
$function kalic0re:dialog/show {name:"$(dialog)"}
