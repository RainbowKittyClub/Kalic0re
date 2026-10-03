# The answer to remove_here's question, numbered from 0, is the trigger's score less 2; the question is
# kept under the player's UUID.
data modify storage kalic0re:parkour tmp.courseBoundaryAnswer.uuid set from entity @s UUID
execute store result storage kalic0re:parkour tmp.courseBoundaryAnswer.index int 1 run \
    scoreboard players get @s kalic0re.admin.remove_course_boundary
function kalic0re:parkour/admin/course_boundary/remove_answer_of with storage kalic0re:parkour tmp.courseBoundaryAnswer
data remove storage kalic0re:parkour tmp.courseBoundaryAnswer
