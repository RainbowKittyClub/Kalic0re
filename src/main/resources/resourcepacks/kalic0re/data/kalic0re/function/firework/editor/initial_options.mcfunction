# For open: the pattern and visibility chosen as the firework has them.
data modify storage kalic0re:dialog built.create_firework.inputs[{key:"pattern"}].options[].initial set value 0b
$data modify storage kalic0re:dialog built.create_firework.inputs[{key:"pattern"}].options[{id:"$(pattern)"}].initial set value 1b
data modify storage kalic0re:dialog built.create_firework.inputs[{key:"particle_mode"}].options[].initial set value 0b
$data modify storage kalic0re:dialog built.create_firework.inputs[{key:"particle_mode"}].options[{id:"$(particleMode)"}].initial set value 1b
