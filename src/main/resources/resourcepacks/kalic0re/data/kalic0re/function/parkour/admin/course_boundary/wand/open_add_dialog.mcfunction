# As the wand's holder, looking straight up: the Add Boundary dialog, its corners the wand's selection, or the
# block at their feet for a corner it lacks, in the selection's dimension, else theirs.
function kalic0re:parkour/admin/course_boundary/wand/read_held
execute align xyz run function kalic0re:parkour/admin/course_boundary/wand/mark_here
data modify storage kalic0re:parkour tmp.wand.firstCorner set from storage kalic0re:parkour tmp.wand.pos
data modify storage kalic0re:parkour tmp.wand.firstCorner set from storage kalic0re:parkour tmp.wand.held.firstCorner
data modify storage kalic0re:parkour tmp.wand.secondCorner set from storage kalic0re:parkour tmp.wand.pos
data modify storage kalic0re:parkour tmp.wand.secondCorner set from storage kalic0re:parkour tmp.wand.held.secondCorner
data modify storage kalic0re:dialog args set value {}
data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour tmp.wand.held.course
data modify storage kalic0re:dialog args.dim set from entity @s Dimension
data modify storage kalic0re:dialog args.dim set from storage kalic0re:parkour tmp.wand.held.dim
function kalic0re:parkour/admin/course_boundary/wand/corner_text {corner:"firstCorner"}
function kalic0re:parkour/admin/course_boundary/wand/corner_text {corner:"secondCorner"}
data remove storage kalic0re:parkour tmp.wand
function kalic0re:parkour/admin/dialog/wand_add_boundary
function kalic0re:dialog/show {name:"wand_add_boundary"}
