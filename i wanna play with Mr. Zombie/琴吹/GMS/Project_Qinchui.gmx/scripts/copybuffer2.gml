/*
appends PART of one buffers data to the end of another
Argument0 = destination buffer;
Argument1 = start pos in source buffer
Argument2 = Number of bytes to copy from source
Argument3 = source buffer
*/
return external_call(global._BufAD, argument0, argument1, argument2, argument3);