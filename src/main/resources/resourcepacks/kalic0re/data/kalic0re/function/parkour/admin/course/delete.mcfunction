# The Delete dialog's button. Runs on it end first, while its lobby still exists to send them to.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$execute as @a[scores={kalic0re.parkour.isActive=1}] at @s run \
    function kalic0re:parkour/admin/course/end_run {course:"$(course)"}
data remove storage kalic0re:parkour tmp.ending
$data remove storage kalic0re:parkour courses[{name:"$(course)"}]
$tellraw @s {text:"Parkour course \"$(course)\" deleted.",color:"green"}
