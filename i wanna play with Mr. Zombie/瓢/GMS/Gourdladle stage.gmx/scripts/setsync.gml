/*
Changes the blocking/non-blocking mode of a socket.

Argument0 = Socket to change.
Argument1 = Mode to set to. 0 = Blocking. 1 = Non-blocking.
*/
return external_call(global._SokF, argument0, argument1);
