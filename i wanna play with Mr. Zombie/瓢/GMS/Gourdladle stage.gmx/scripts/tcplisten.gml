/*
  Creates a listening socket to monitor a certain port for incoming
  connections.
  Argument0 = Port to listen on
  Argument1 = Max amount of people allowed to be in the connected but not
  accepted list
  Argument2 = Blocking(0)/Non-Blocking(1) mode. Affects the tcpaccept script.
  
  returns the id of the socket or a negative error code.
*/
return external_call(global._SokB, argument0, argument1, argument2);
