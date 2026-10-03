# Secret discovery: each note a bell and a bit together, 0.18 s apart. Played by
# kalic0re:effect/sound/play {effect:"kalic0re:effect/sound/secret_discovery_01",targets:…,location:…}.
# Each sound plays delay seconds after the one before it, on the nearest tick.
data modify storage kalic0re:effect sound.notes set value [ \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:1.06,delay:0.0}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:1.06,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:1.0,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:1.0,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:0.84,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:0.84,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:0.59,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:0.59,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:0.56,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:0.56,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:0.89,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:0.89,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:1.12,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:1.12,delay:0.0}, \
    {sound:"minecraft:block.note_block.bell",source:"block",volume:2.0,pitch:1.41,delay:0.18}, \
    {sound:"minecraft:block.note_block.bit",source:"block",volume:1.0,pitch:1.41,delay:0.0}]
