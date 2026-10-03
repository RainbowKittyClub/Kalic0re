# Choice $(index) of the last question this player was asked: the boundary it showed, if its course still
# has it. The question stays, so they can pick another of its boundaries next.
$execute unless data storage kalic0re:parkour courseBoundaryRemovalQuestions[{uuid:$(uuid)}].choices[$(index)] run return run \
    function kalic0re:parkour/admin/course_boundary/message/answer_out_of_date
$data modify storage kalic0re:parkour tmp.boundaryRemoval set from \
    storage kalic0re:parkour courseBoundaryRemovalQuestions[{uuid:$(uuid)}].choices[$(index)]
function kalic0re:parkour/admin/course_boundary/remove_found
