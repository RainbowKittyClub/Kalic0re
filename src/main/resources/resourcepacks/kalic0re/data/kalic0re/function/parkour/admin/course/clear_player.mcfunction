# The Clear a Player's Time dialog's button, which then shows the Clear Leaderboard list again.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].times[{player:"$(player)"}] run return run \
    function kalic0re:parkour/admin/course/no_time {course:"$(course)",player:"$(player)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].times[{player:"$(player)"}]
$function kalic0re:parkour/course/reset_best_score {course:"$(course)",player:"$(player)"}
$execute as @a[name=$(player),scores={kalic0re.parkour.isActive=1}] run \
    function kalic0re:parkour/admin/course/runner {course:"$(course)",run:"kalic0re:parkour/timer/forget_shown_best"}
data remove storage kalic0re:parkour tmp.runner
$tellraw @s {text:"Cleared $(player)'s time on parkour course \"$(course)\".",color:"green"}
$function kalic0re:parkour/admin/course/times {course:"$(course)"}
