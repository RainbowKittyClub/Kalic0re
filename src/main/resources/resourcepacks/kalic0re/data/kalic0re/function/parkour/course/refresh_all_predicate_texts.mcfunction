# Every course's standing-block predicate rebuilt and its predicate texts with it, at load: courses saved
# before a rule changed, or before texts were kept, catch up, and none can fall behind.
function kalic0re:parkour/course/refresh_predicate_texts_at {index:0}
data remove storage kalic0re:parkour tmp.predicateTexts
