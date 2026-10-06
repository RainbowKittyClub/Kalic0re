# For join: the next number on the text, after a comma unless it's the first, then the rest.
execute unless data storage kalic0re:firework tmp.join.rest[0] run return 0
data modify storage kalic0re:firework tmp.join.next set from storage kalic0re:firework tmp.join.rest[0]
data remove storage kalic0re:firework tmp.join.rest[0]
execute store result score #joinFirst kalic0re.firework.tmp if data storage kalic0re:firework tmp.join{text:""}
execute if score #joinFirst kalic0re.firework.tmp matches 1 run function kalic0re:firework/editor/join_first with storage kalic0re:firework tmp.join
execute if score #joinFirst kalic0re.firework.tmp matches 0 run function kalic0re:firework/editor/join_more with storage kalic0re:firework tmp.join
function kalic0re:firework/editor/join_next
