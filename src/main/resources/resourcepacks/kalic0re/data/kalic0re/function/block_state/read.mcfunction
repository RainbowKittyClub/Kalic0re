# Reads the block here into storage kalic0re:block_state state, in the form a block_display's
# block_state takes: {Name:"<id>",Properties:{<name>:"<value>",…}}. Fails, with state removed, on a
# block the Minecraft the pack was built from doesn't have, such as a modded one.
data remove storage kalic0re:block_state state
return run function kalic0re:block_state/lookup/root
