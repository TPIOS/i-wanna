/*
Returns the read or write pos
Argument0 = read/write. 0 = write pos. 1 = read pos
[Argument1]: The buffer id to use. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufO, argument0, argument1);
