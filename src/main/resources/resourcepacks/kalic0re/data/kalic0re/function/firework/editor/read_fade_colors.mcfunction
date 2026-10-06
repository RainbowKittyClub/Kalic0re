# For read: the Fade Colors box, comma-separated numbers, as edited.fadeColors; empty is none, fading to the colours.
execute if data storage kalic0re:firework input.texts{fadeColors:""} run \
    return run data modify storage kalic0re:firework edited.fadeColors set value [I;]
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:firework input.texts.fadeColors
execute unless function kalic0re:text/only_color_list run return fail
data modify storage kalic0re:firework tmp.colorList set value {box:"fadeColors"}
data modify storage kalic0re:firework tmp.colorList.text set from storage kalic0re:firework input.texts.fadeColors
function kalic0re:firework/editor/color_list_into with storage kalic0re:firework tmp.colorList
data remove storage kalic0re:firework tmp.colorList
execute unless data storage kalic0re:firework edited.fadeColors run return fail
return 1
