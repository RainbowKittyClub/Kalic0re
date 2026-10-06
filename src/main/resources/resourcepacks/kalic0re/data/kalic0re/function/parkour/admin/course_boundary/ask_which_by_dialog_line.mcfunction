$function kalic0re:parkour/admin/course_boundary/checkbox/$(key) {key:"c$(index)",from:"$(x1) $(y1) $(z1)",to:"$(x2) $(y2) $(z2)"}
$data modify storage kalic0re:parkour tmp.boundaryHere.line.checks set value "$(checks)$(separator)$(dollar)(c$(index))"
data modify storage kalic0re:parkour tmp.boundaryHere.line.separator set value ","
