# The chest's entries become course $(courseId)'s list, and what it has shown.
$data modify storage kalic0re:parkour courses[{id:$(courseId)}].standingBlocks set from storage kalic0re:parkour tmp.chest.entries
data modify storage kalic0re:parkour tmp.chest.record.shown set from storage kalic0re:parkour tmp.chest.entries
$function kalic0re:parkour/admin/standing_block/rebuild_predicate {course:"$(course)"}
