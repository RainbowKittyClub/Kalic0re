# The root, $(rootX) $(rootY) $(rootZ) from the lowest block: relative, as positioned would centre a whole
# x or z, and the macro writes a whole number without its .0.
$execute positioned $(x) $(y) $(z) align xyz positioned ~$(rootX) ~$(rootY) ~$(rootZ) \
    summon minecraft:block_display run \
    function kalic0re:effect/display/create/root with storage kalic0re:effect displayCreate.place
