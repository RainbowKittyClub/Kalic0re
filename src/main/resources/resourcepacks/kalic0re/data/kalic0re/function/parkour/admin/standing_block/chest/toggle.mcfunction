# The pane was taken: $(course) switches mode, told to the chest's owner by mode itself.
$execute if data storage kalic0re:parkour tmp.chest{mode:"allow"} as @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/standing_block/mode {course:"$(course)",mode:"deny"}
$execute unless data storage kalic0re:parkour tmp.chest{mode:"allow"} as @a[tag=kalic0re.parkour.standingBlockChestEditor,limit=1] run \
    function kalic0re:parkour/admin/standing_block/mode {course:"$(course)",mode:"allow"}
$data modify storage kalic0re:parkour tmp.chest.mode set from storage kalic0re:parkour courses[{name:"$(course)"}].standingBlockMode
