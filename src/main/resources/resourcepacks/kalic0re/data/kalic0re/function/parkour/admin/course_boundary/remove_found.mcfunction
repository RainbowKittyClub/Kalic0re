# Removes the boundary in tmp.boundaryRemoval, as collect_here found it, from its course. Found again by
# its predicate term: a boundary removed since, here or by someone else, moves the rest up the list.
data modify storage kalic0re:parkour tmp.boundaryRemoval.dim set value ""
data modify storage kalic0re:parkour tmp.boundaryRemoval.dim set from storage kalic0re:parkour tmp.boundaryRemoval.boundary.dim
function kalic0re:parkour/admin/course_boundary/find_term with storage kalic0re:parkour tmp.boundaryRemoval
execute unless data storage kalic0re:parkour tmp.boundaryRemoval.index run \
    function kalic0re:parkour/admin/course_boundary/message/answer_out_of_date
execute if data storage kalic0re:parkour tmp.boundaryRemoval.index run \
    function kalic0re:parkour/admin/course_boundary/remove_at with storage kalic0re:parkour tmp.boundaryRemoval
data remove storage kalic0re:parkour tmp.boundaryRemoval
