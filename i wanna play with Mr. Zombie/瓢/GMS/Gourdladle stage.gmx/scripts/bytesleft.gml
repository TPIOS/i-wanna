/*
Returns how much bytes are left to read. Buffsize-Buffreadpos.
[Argument0]: The buffer id to check how many bytes are left. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufAE, argument0);
