# For read_particle: 1 when $(sparkParticle) reads as a particle, never shown; alone, so one that doesn't fails only this.
$execute if entity @s[tag=kalic0re.firework.neverTagged] run particle $(sparkParticle)
return 1
