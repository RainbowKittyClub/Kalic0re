# As a runner, at their feet. Burning means they touched fire or lava, which ends the attempt like
# leaving the boundaries does, boundaries or not.
execute if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_on_fire:true}}} run \
    return run function kalic0re:parkour/course_boundary/burning
# The course id from their score, so the check needs no UUID lookup.
execute store result storage kalic0re:parkour tmp.courseBoundaryCheck.id int 1 run \
    scoreboard players get @s kalic0re.parkour.runningCourseId
function kalic0re:parkour/course_boundary/check with storage kalic0re:parkour tmp.courseBoundaryCheck
data remove storage kalic0re:parkour tmp.courseBoundaryCheck
