/*
  Sends the data in a buffer through the selected tcp or udp socket.
  Argument0 = Socket to send to.
  [Argument1] = Ip to send to (for udp sockets)
  [Argument2] = Port to send to (for udp socket)
  [Argument3]: The buffer id to send. Leave out this argument or set
it to 0 to use the default buffer.
  
  Returns the amount of bytes sent + the bytes used to format the message
  NOTE:
  If you're sending over a tcp socket and dont want to use the default buffer
  than send a message like this:
  sendmessage(sockid, "", 0, bufferid);
*/
return external_call(global._SokD, argument0, argument1, argument2, argument3);
