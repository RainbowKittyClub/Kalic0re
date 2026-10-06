# As a player: the Create Firework dialog built for storage kalic0re:firework editor: {firework (any setting left out
# the defaults'), save (the function Save runs with {context}), context}. The caller sets its exit and shows it.
data modify storage kalic0re:firework tmp.editing set from storage kalic0re:firework defaults
data modify storage kalic0re:firework tmp.editing merge from storage kalic0re:firework editor.firework
# Empty boxes show what they stand for, but the blast sound's, whose empty is vanilla's for the pattern.
execute if data storage kalic0re:firework tmp.editing{sparkParticle:""} run \
    data modify storage kalic0re:firework tmp.editing.sparkParticle set from storage kalic0re:firework defaults.sparkParticle
execute if data storage kalic0re:firework tmp.editing{launchSound:""} run \
    data modify storage kalic0re:firework tmp.editing.launchSound set from storage kalic0re:firework defaults.launchSound
execute if data storage kalic0re:firework tmp.editing{twinkleSound:""} run \
    data modify storage kalic0re:firework tmp.editing.twinkleSound set from storage kalic0re:firework defaults.twinkleSound
data modify storage kalic0re:dialog args set value {}
function kalic0re:firework/editor/join {from:"colors",into:"colors_text"}
function kalic0re:firework/editor/join {from:"fadeColors",into:"fade_colors_text"}
function kalic0re:firework/editor/text_args with storage kalic0re:firework tmp.editing
data modify storage kalic0re:dialog args.save set from storage kalic0re:firework editor.save
data modify storage kalic0re:dialog args.save_context set from storage kalic0re:firework editor.context
function kalic0re:dialog/create_firework
# Each slider where the firework is, held inside the range create_firework.json gives it.
function kalic0re:firework/editor/initial {key:"radius",path:"radius"}
function kalic0re:firework/editor/initial {key:"rise_ticks",path:"riseTicks"}
function kalic0re:firework/editor/initial {key:"burst_ticks",path:"burstTicks"}
function kalic0re:firework/editor/initial {key:"dust_scale",path:"dustScale"}
function kalic0re:firework/editor/initial {key:"spark_speed",path:"sparkSpeed"}
function kalic0re:firework/editor/initial {key:"twinkle_delay_ticks",path:"twinkleDelayTicks"}
function kalic0re:firework/editor/initial {key:"twinkle_sparks",path:"twinkleSparks"}
function kalic0re:firework/editor/initial {key:"twinkle_spread",path:"twinkleSpread"}
function kalic0re:firework/editor/initial {key:"sound_volume",path:"soundVolume"}
function kalic0re:firework/editor/initial_toggles
function kalic0re:firework/editor/initial_options with storage kalic0re:firework tmp.editing
data remove storage kalic0re:firework tmp.editing
