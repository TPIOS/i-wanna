/// argument0 size
/// argument1 value

var fill = argument1;
var size = argument0;
var a = array_create(size);
for (var i = 0; i < size-1; i++) {
    a[i] = fill;
}

return(a)
