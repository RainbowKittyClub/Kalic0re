# Run as a player, e.g. from a start plate's command block: function kalic0re:parkour/start {course:"<name>"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].start run return run \
    function kalic0re:parkour/course/missing {course:"$(course)",point:"start"}
$data modify storage kalic0re:parkour tmp.course set value "$(course)"
# The course's id, which renames keep, on a score the tick reads without the runner's UUID.
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$execute store result score @s kalic0re.parkour.runningCourseId run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].id
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/begin"}
scoreboard players set @s kalic0re.parkour.runTicks 0
scoreboard players set @s kalic0re.parkour.isActive 1
