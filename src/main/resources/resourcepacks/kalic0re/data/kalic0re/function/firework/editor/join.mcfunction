# For open: tmp.editing.$(from), a list of numbers, as kalic0re:dialog args.$(into), "1,2,3": bare commas, so it's
# never longer than the text that saved it, which fit the box.
data modify storage kalic0re:firework tmp.join set value {text:"",rest:[]}
$data modify storage kalic0re:firework tmp.join.rest set from storage kalic0re:firework tmp.editing.$(from)
function kalic0re:firework/editor/join_next
$data modify storage kalic0re:dialog args.$(into) set from storage kalic0re:firework tmp.join.text
data remove storage kalic0re:firework tmp.join
