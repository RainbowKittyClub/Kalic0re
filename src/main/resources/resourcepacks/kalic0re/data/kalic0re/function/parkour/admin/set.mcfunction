# /function kalic0re:parkour/admin/set {course:"<name>",point:"start"}, or point "lobby"; stand where it goes.
# The list filter adds {name:"<name>"} when no course matches, so a new name makes a new course.
function kalic0re:parkour/pos/capture
$function kalic0re:parkour/admin/store_point {course:"$(course)",point:"$(point)"}
