/*
Frees (deletes) a buffer. You cannot free the default buffer (0)
Argument0: buffer id
*/
return external_call(global._BufX, argument0);