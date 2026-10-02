# Fails to instantiate, so returns no success, unless $(block) parses as a block.
$execute if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{stepping_on:{block:{blocks:"$(block)"}}}} run return 1
return 1
