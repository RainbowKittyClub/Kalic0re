# The Clear All Times dialog's button: empties $(course)'s leaderboard and best-times scoreboard.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].times
$function kalic0re:parkour/course/reset_best_score {course:"$(course)",player:"*"}
$function kalic0re:parkour/admin/course/each_runner {course:"$(course)",run:"kalic0re:parkour/timer/forget_shown_best"}
$tellraw @s {translate:"kalic0re.parkour.chat.cleared_all",fallback:"Cleared every time on parkour course \"%s\".",with:["$(course)"],color:"green"}
