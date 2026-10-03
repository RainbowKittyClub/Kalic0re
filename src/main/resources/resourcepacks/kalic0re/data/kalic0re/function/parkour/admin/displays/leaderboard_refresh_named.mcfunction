# leaderboard_refresh for $(course) by name; a course with no id has never had a board.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].id run \
    function kalic0re:parkour/admin/displays/leaderboard_refresh with storage kalic0re:parkour courses[{name:"$(course)"}]
