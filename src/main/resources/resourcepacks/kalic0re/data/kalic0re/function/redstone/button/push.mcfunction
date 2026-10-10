# For push_button and push_button_for, in their caller's context, so position is read once: the button at
# $(position) pushed for #buttonPushTicks. Fails, changing nothing, unless it's loaded and an unpressed
# button.
$execute positioned $(position) unless loaded ~ ~ ~ run return fail
$execute positioned $(position) unless block ~ ~ ~ #minecraft:buttons[powered=false] run return fail
$execute positioned $(position) align xyz positioned ~.5 ~.5 ~.5 run \
    return run function kalic0re:redstone/button/press
