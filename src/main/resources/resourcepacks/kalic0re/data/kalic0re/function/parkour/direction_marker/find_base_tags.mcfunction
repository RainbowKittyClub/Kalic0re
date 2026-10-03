# Each of tmp.directionMarkerTags that's a base tag sets that axis's base score. A shorter tag fails the substring
# and leaves no prefix.
execute unless data storage kalic0re:parkour tmp.directionMarkerTags[0] run return 0
data modify storage kalic0re:parkour tmp.directionMarkerTag set value {}
data modify storage kalic0re:parkour tmp.directionMarkerTag.prefix set string storage kalic0re:parkour tmp.directionMarkerTags[0] 0 36
data modify storage kalic0re:parkour tmp.directionMarkerTag.axis set string storage kalic0re:parkour tmp.directionMarkerTags[0] 36 37
data modify storage kalic0re:parkour tmp.directionMarkerTag.value set string storage kalic0re:parkour tmp.directionMarkerTags[0] 38
execute if data storage kalic0re:parkour tmp.directionMarkerTag{prefix:"kalic0re.parkour.directionMarkerBase"} run \
    function kalic0re:parkour/direction_marker/read_base_tag with storage kalic0re:parkour tmp.directionMarkerTag
data remove storage kalic0re:parkour tmp.directionMarkerTag
data remove storage kalic0re:parkour tmp.directionMarkerTags[0]
function kalic0re:parkour/direction_marker/find_base_tags
