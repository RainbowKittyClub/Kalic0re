# The Add Boundary dialog's Add: each corner's text read as a position, as `positioned` reads one, so ~ counts
# from where you stand; then $(boundaryType), add or add_exclusion, with those blocks.
data remove storage kalic0re:parkour tmp.boundaryDialog
$function kalic0re:parkour/admin/course_boundary/read_corner_text {text:"$(firstCorner)",corner:"firstCorner"}
$function kalic0re:parkour/admin/course_boundary/read_corner_text {text:"$(secondCorner)",corner:"secondCorner"}
$execute unless data storage kalic0re:parkour tmp.boundaryDialog.firstCorner run return run \
    function kalic0re:parkour/admin/course_boundary/refuse_corner_text {text:"$(firstCorner)"}
$execute unless data storage kalic0re:parkour tmp.boundaryDialog.secondCorner run return run \
    function kalic0re:parkour/admin/course_boundary/refuse_corner_text {text:"$(secondCorner)"}
$data modify storage kalic0re:parkour tmp.boundaryDialog.args set value {course:"$(course)",dim:"$(dim)"}
data modify storage kalic0re:parkour tmp.boundaryDialog.args.x1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[0]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.y1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[1]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.z1 set from storage kalic0re:parkour tmp.boundaryDialog.firstCorner[2]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.x2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[0]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.y2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[1]
data modify storage kalic0re:parkour tmp.boundaryDialog.args.z2 set from storage kalic0re:parkour tmp.boundaryDialog.secondCorner[2]
$function kalic0re:parkour/admin/course_boundary/$(boundaryType) with storage kalic0re:parkour tmp.boundaryDialog.args
data remove storage kalic0re:parkour tmp.boundaryDialog
