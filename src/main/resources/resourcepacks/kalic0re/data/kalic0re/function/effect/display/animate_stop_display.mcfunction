# execute as <display> run function kalic0re:effect/display/animate_stop_display
# e.g. execute as @e[tag=logo] run function kalic0re:effect/display/animate_stop_display
# Stops this display's move or bounce, leaving it where it is. Fails if it has neither.
execute unless entity @s[tag=kalic0re.effect.displayMoving] unless entity @s[tag=kalic0re.effect.displayBouncing] run \
    return fail
execute if entity @s[tag=kalic0re.effect.displayMoving] run function kalic0re:effect/display/move/finish
execute if entity @s[tag=kalic0re.effect.displayBouncing] run function kalic0re:effect/display/bounce/finish
return 1
