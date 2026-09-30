# Splits #ticks and puts its digits in kalic0re:dialog args, for a dialog piece showing the time.
function kalic0re:parkour/timer/split
execute store result storage kalic0re:dialog args.tensOfMinutes int 1 run scoreboard players get #tensOfMinutes kalic0re.parkour.tmp
execute store result storage kalic0re:dialog args.onesOfMinutes int 1 run scoreboard players get #onesOfMinutes kalic0re.parkour.tmp
execute store result storage kalic0re:dialog args.tensOfSeconds int 1 run scoreboard players get #tensOfSeconds kalic0re.parkour.tmp
execute store result storage kalic0re:dialog args.onesOfSeconds int 1 run scoreboard players get #onesOfSeconds kalic0re.parkour.tmp
execute store result storage kalic0re:dialog args.tenthsOfSecond int 1 run scoreboard players get #tenthsOfSecond kalic0re.parkour.tmp
