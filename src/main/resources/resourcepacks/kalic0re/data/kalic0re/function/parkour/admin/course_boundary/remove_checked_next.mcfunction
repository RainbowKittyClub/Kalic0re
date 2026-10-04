execute unless data storage kalic0re:parkour tmp.boundaryChecks.checks[0] run return 0
execute store result storage kalic0re:parkour tmp.boundaryChecks.index int 1 run \
    scoreboard players get #boundaryCheckIndex kalic0re.parkour.tmp
execute store result score #boundaryChecked kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.boundaryChecks.checks[0]
execute if score #boundaryChecked kalic0re.parkour.tmp matches 1 run scoreboard players add #boundariesChecked kalic0re.parkour.tmp 1
execute if score #boundaryChecked kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/course_boundary/remove_answer_of with storage kalic0re:parkour tmp.boundaryChecks
data remove storage kalic0re:parkour tmp.boundaryChecks.checks[0]
scoreboard players add #boundaryCheckIndex kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/remove_checked_next
