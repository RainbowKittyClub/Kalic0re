# The campfire was taken: $(course) switches burning, told to the chest's owner by burning itself.
$execute if data storage kalic0re:parkour tmp.chest{burning:"allow"} as @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/course/burning {course:"$(course)",mode:"deny"}
$execute unless data storage kalic0re:parkour tmp.chest{burning:"allow"} as @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/course/burning {course:"$(course)",mode:"allow"}
$data modify storage kalic0re:parkour tmp.chest.burning set from storage kalic0re:parkour courses[{name:"$(course)"}].burningMode
