/*
  Returns the amount of bytes in a buffer.
  [Argument0]: The buffer id to use. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufQ, argument0);