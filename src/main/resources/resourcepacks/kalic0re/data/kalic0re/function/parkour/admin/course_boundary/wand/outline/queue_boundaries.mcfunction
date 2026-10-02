# $(course)'s boundaries, then its exclusion boundaries, for draw_queued_boundaries to outline from the
# first. By id, which renames keep.
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$execute store result score @s kalic0re.parkour.boundaryOutlineCourseId run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].id
tag @s remove kalic0re.parkour.boundaryOutlineDrawingExclusionBoundaries
scoreboard players set @s kalic0re.parkour.boundaryOutlineNextBoundary 0
