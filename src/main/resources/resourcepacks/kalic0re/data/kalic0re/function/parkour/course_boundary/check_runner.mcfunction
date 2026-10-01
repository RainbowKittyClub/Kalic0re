# As a runner, at their feet. The course id from their score, so the check needs no UUID lookup.
execute store result storage kalic0re:parkour tmp.courseBoundaryCheck.id int 1 run \
    scoreboard players get @s kalic0re.parkour.runningCourseId
function kalic0re:parkour/course_boundary/check with storage kalic0re:parkour tmp.courseBoundaryCheck
data remove storage kalic0re:parkour tmp.courseBoundaryCheck
