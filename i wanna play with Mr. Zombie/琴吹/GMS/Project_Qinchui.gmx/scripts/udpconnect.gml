/*
Creates a udp socket.
Argument0 = Port to use.
Argument1 = Blocking/non-blocking mode.

HOW TO SEND MESSAGES:
sendmessage(sockid, "ip.to.send.to", portosendto, [bufferid]);
*/
return external_call(global._SokH, argument0, argument1);