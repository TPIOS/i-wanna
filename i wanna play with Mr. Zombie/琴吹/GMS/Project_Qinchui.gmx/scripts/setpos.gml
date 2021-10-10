/*
  Sets the read/write pos in a buffer
  Argument0 = pos.
  [Argument1]: The buffer id to use. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufN, argument0, argument1);