# As a player who used /trigger kalic0re.admin.remove_course_boundary: 1, the trigger alone, removes the
# boundary around their feet, asking which when several hold them; 2 and up are the answers' links. Enabled
# again after, since using a trigger disables it.
scoreboard players remove @s kalic0re.admin.remove_course_boundary 2
execute if score @s kalic0re.admin.remove_course_boundary matches ..-1 run \
    function kalic0re:parkour/admin/course_boundary/remove_here
execute if score @s kalic0re.admin.remove_course_boundary matches 0.. run \
    function kalic0re:parkour/admin/course_boundary/remove_answer
scoreboard players set @s kalic0re.admin.remove_course_boundary 0
scoreboard players enable @s kalic0re.admin.remove_course_boundary
