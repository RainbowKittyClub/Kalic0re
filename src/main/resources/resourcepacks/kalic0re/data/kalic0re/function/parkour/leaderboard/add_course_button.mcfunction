# A course nobody has finished yet gets its number here, so its button can open its (empty) leaderboard.
$function kalic0re:parkour/course/ensure_id {course:"$(course)"}
$data modify storage kalic0re:dialog args.id set from storage kalic0re:parkour courses[{name:"$(course)"}].id
function kalic0re:parkour/leaderboard/leaderboard_button
data modify storage kalic0re:dialog built.leaderboards.actions append from storage kalic0re:dialog built.leaderboard_button
