# /function kalic0re:firework/launch, as and at whatever sets it off: the firework in storage kalic0re:firework launch,
# any setting it leaves out the defaults'. It bursts at its position (~ counts from here), its rocket rising from here.
data modify storage kalic0re:firework tmp.firework set from storage kalic0re:firework defaults
data modify storage kalic0re:firework tmp.firework merge from storage kalic0re:firework launch
function kalic0re:firework/resolve
function kalic0re:firework/launch_at with storage kalic0re:firework tmp.firework
data remove storage kalic0re:firework tmp.firework
schedule function kalic0re:firework/advance 1t replace
