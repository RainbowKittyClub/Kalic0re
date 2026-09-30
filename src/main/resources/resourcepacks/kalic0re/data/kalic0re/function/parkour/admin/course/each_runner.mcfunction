# Runs $(run) {uuid,course} as each online player running $(course).
$execute as @a[scores={kalic0re.parkour.isActive=1}] at @s run \
    function kalic0re:parkour/admin/course/runner {course:"$(course)",run:"$(run)"}
data remove storage kalic0re:parkour tmp.runner
