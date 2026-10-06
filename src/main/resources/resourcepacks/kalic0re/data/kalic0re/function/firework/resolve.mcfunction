# For launch: tmp.firework's empty boxes as the defaults, no fade colours as its colours, and an empty blast sound as
# vanilla's: a large blast for a large ball, a blast otherwise.
execute unless data storage kalic0re:firework tmp.firework.colors[0] run \
    data modify storage kalic0re:firework tmp.firework.colors set from storage kalic0re:firework defaults.colors
execute unless data storage kalic0re:firework tmp.firework.fadeColors[0] run \
    data modify storage kalic0re:firework tmp.firework.fadeColors set from storage kalic0re:firework tmp.firework.colors
execute if data storage kalic0re:firework tmp.firework{sparkParticle:""} run \
    data modify storage kalic0re:firework tmp.firework.sparkParticle set from storage kalic0re:firework defaults.sparkParticle
execute if data storage kalic0re:firework tmp.firework{launchSound:""} run \
    data modify storage kalic0re:firework tmp.firework.launchSound set from storage kalic0re:firework defaults.launchSound
execute if data storage kalic0re:firework tmp.firework{twinkleSound:""} run \
    data modify storage kalic0re:firework tmp.firework.twinkleSound set from storage kalic0re:firework defaults.twinkleSound
execute if data storage kalic0re:firework tmp.firework{blastSound:"",pattern:"large_ball"} run \
    data modify storage kalic0re:firework tmp.firework.blastSound set value "minecraft:entity.firework_rocket.large_blast"
execute if data storage kalic0re:firework tmp.firework{blastSound:""} run \
    data modify storage kalic0re:firework tmp.firework.blastSound set value "minecraft:entity.firework_rocket.blast"
