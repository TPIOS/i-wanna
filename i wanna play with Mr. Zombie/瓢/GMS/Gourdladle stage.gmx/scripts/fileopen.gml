/*
  Opens or creates a file for reading or writing.
  Argument0 = Filename.
  Argument1 = mode. 0 = reading, 1 = writing, 2 = both reading and writing
  Returns a file id.   
*/
return external_call(global._FilA, argument0, argument1);
