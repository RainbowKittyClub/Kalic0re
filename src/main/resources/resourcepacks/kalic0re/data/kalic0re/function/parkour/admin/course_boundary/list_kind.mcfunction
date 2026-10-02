# A heading, then a line per boundary in $(course)'s $(boundaries), each with a link to $(remove) it;
# nothing when there are none.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)[0] run return 0
$tellraw @s {translate:"kalic0re.parkour.chat.$(heading)",fallback:"$(headingName) of parkour course \"%s\":",with:["$(course)"]}
$data modify storage kalic0re:parkour tmp.courseBoundaryList set value {course:"$(course)",boundary:1,remove:"$(remove)"}
$data modify storage kalic0re:parkour tmp.courseBoundaryList.boundaries set from storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)
function kalic0re:parkour/admin/course_boundary/list_next
data remove storage kalic0re:parkour tmp.courseBoundaryList
