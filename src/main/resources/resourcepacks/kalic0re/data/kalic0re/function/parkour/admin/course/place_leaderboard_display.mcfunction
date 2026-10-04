# The Place Leaderboard Display dialog's button: a board of size $(size) for $(course), its title at the texts'
# position, facing their rotation (admin/text/place_readable).
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
execute unless function kalic0re:parkour/admin/text/place_readable run return run function kalic0re:parkour/admin/text/refuse_place
$data modify storage kalic0re:parkour tmp.dialogTexts merge value {course:"$(course)",size:$(size)}
function kalic0re:parkour/admin/displays/leaderboard_sized with storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.dialogTexts
