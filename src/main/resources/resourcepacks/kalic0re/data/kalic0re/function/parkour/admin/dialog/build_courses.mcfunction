# The Courses dialog: New Course, embedded so it opens client-side, Edit Nearby Leaderboard Display, then a
# button per course.
function kalic0re:parkour/admin/dialog/new_course
function kalic0re:parkour/admin/dialog/courses
data modify storage kalic0re:dialog built.courses.actions[{label:{translate:"kalic0re.parkour.menu.courses.new_course"}}].action.dialog \
    set from storage kalic0re:dialog built.new_course
data modify storage kalic0re:parkour tmp.names set value []
data modify storage kalic0re:parkour tmp.names append from storage kalic0re:parkour courses[].name
function kalic0re:parkour/admin/dialog/add_course_buttons
data remove storage kalic0re:parkour tmp.names
