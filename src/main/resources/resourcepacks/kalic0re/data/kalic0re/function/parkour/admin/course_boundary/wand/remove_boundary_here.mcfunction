# As the wand's holder, looking straight down: as remove_here, the boundary of the wand's course holding their
# feet removed, but more than one is asked about in the Remove Boundaries dialog rather than in chat.
function kalic0re:parkour/admin/course_boundary/wand/read_held
data modify storage kalic0re:parkour tmp.wand.held.askWhich set value "ask_which_by_dialog"
function kalic0re:parkour/admin/course_boundary/remove_here_on with storage kalic0re:parkour tmp.wand.held
data remove storage kalic0re:parkour tmp.wand
