# For read: the Colors box, comma-separated numbers, as edited.colors, at least one.
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:firework input.texts.colors
execute unless function kalic0re:text/only_color_list run return fail
data modify storage kalic0re:firework tmp.colorList set value {box:"colors"}
data modify storage kalic0re:firework tmp.colorList.text set from storage kalic0re:firework input.texts.colors
function kalic0re:firework/editor/color_list_into with storage kalic0re:firework tmp.colorList
data remove storage kalic0re:firework tmp.colorList
execute unless data storage kalic0re:firework edited.colors[0] run return fail
return 1
