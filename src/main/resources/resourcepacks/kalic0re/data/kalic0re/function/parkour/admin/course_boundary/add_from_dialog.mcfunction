# The Add Boundary dialog's Add: the blocks the texts' firstCorner and secondCorner are in, then $(boundaryType),
# add or add_exclusion, with them.
data remove storage kalic0re:parkour tmp.boundaryDialog
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
scoreboard players set #boundaryCornerRead kalic0re.parkour.tmp 0
execute store result score #boundaryCornerRead kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/read_corner_text {corner:"firstCorner"}
execute if score #boundaryCornerRead kalic0re.parkour.tmp matches 0 run return run \
    function kalic0re:parkour/admin/course_boundary/refuse_corner_text {corner:"firstCorner"}
scoreboard players set #boundaryCornerRead kalic0re.parkour.tmp 0
execute store result score #boundaryCornerRead kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/read_corner_text {corner:"secondCorner"}
execute if score #boundaryCornerRead kalic0re.parkour.tmp matches 0 run return run \
    function kalic0re:parkour/admin/course_boundary/refuse_corner_text {corner:"secondCorner"}
data remove storage kalic0re:parkour tmp.dialogTexts
$data modify storage kalic0re:parkour tmp.boundaryDialog.args set value {course:"$(course)",dim:"$(dim)"}
data modify storage kalic0re:parkour tmp.boundaryDialog.args.x1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[0]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.y1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[1]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.z1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[2]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.x2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[0]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.y2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[1]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.z2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[2]
$function kalic0re:parkour/admin/course_boundary/$(boundaryType) with storage kalic0re:parkour tmp.boundaryDialog.args
data remove storage kalic0re:parkour tmp.boundaryDialog
