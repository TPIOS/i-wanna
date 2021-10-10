/*
    Returns the last incoming IP received from any of the sockets.
    It will return an IP from the following situations:
    1. When you receive a message, lastinIP() will return the IP
    address of the person who sent that message.
    2. When you've just accepted a connection with tcpaccept(), lastinIP()
    will return the ip address of the computer that was accepted.  
*/
return external_call(global._SokJ);