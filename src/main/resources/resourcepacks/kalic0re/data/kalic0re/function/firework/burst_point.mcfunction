# For sparks at the burst: a spark of the colour gliding out to its point over burstTicks, and with a trail, a
# $(sparkParticle) thrown the same way, a firework spark's falling and fading by default.
$particle minecraft:trail{target:[$(tx)d,$(ty)d,$(tz)d],color:$(color),duration:$(burstTicks)} ~ ~ ~ 0 0 0 0 1 $(particleMode)
$execute if data storage kalic0re:firework tmp.spark{hasTrail:1b} run \
    particle $(sparkParticle) ~ ~ ~ $(dx) $(dy) $(dz) $(sparkSpeed) 0 $(particleMode)
