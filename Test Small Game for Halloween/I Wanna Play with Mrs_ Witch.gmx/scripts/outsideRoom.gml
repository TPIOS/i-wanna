//check whether player is outside of the room

var obj;
obj = set_default(argument0, id)

if (obj.x < 0) {
    return true
}
if (obj.x > room_width) {
    return true
}
if (obj.y < 0) {
    return true
}
if (obj.y > room_height) {
    return true
}
return false

