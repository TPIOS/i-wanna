/*
    Returns the last incoming port number received from any of the sockets.
    It will return a port number from the following situations:
    1. When you receive a message, lastinPort() will return the senders
    port number that theyre using to send the message. 
*/
return external_call(global._SokY);