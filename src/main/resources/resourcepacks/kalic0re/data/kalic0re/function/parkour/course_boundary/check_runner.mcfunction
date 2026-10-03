# As a runner, at their feet, every tick. The course id from their score, so the check needs no UUID lookup.
# Each runner's id overwrites the last; kalic0re:parkour/tick removes it once, after them all.
execute store result storage kalic0re:parkour tmp.courseBoundaryCheck.id int 1 run \
    scoreboard players get @s kalic0re.parkour.runningCourseId
function kalic0re:parkour/course_boundary/check with storage kalic0re:parkour tmp.courseBoundaryCheck
