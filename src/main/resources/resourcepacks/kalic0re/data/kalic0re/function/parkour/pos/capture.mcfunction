# Every "from entity" saves the whole player, so take one snapshot and read the fields from storage.
data modify storage kalic0re:parkour tmp.self set from entity @s
data modify storage kalic0re:parkour tmp.pos set value {}
data modify storage kalic0re:parkour tmp.pos.x set from storage kalic0re:parkour tmp.self.Pos[0]
data modify storage kalic0re:parkour tmp.pos.y set from storage kalic0re:parkour tmp.self.Pos[1]
data modify storage kalic0re:parkour tmp.pos.z set from storage kalic0re:parkour tmp.self.Pos[2]
data modify storage kalic0re:parkour tmp.pos.yaw set from storage kalic0re:parkour tmp.self.Rotation[0]
data modify storage kalic0re:parkour tmp.pos.pitch set from storage kalic0re:parkour tmp.self.Rotation[1]
data modify storage kalic0re:parkour tmp.pos.dim set from storage kalic0re:parkour tmp.self.Dimension
data remove storage kalic0re:parkour tmp.self
