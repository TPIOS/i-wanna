/*
  Converts a ip string (ie 202.13.23.1) to a number.
  The number is the right size to be written as a unsigned int
  in a buffer.
  argument0 = ip address
  returns: number
*/
return external_call(global._UtilE, argument0);