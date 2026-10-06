# For read: the Rocket Color box, one number, as edited.riseColor.
execute if data storage kalic0re:firework input.texts{riseColor:""} run return fail
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:firework input.texts.riseColor
execute unless function kalic0re:text/only_digits run return fail
function kalic0re:firework/editor/rise_color_into with storage kalic0re:firework input.texts
execute unless data storage kalic0re:firework edited.riseColor run return fail
return 1
