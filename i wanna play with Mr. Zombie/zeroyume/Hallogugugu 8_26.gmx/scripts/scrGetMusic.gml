///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
    case rStage01:
    //case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
        roomSong = musGuyRock;
        break;                      //make sure to always put a break after setting the song
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;
    case rEnd:
        roomSong = -1;              //play nothing
        break;
        
    case room00:
    case room01:
        roomSong = musTemplar;
        break;  
        
    case room02:
    case room03:
    case room04:
    case room05:
    case room06:
    case room07:
    case room08:
    case room09:
    case room10:
    case room11:
    case room12:
         roomSong = musGhost;
        break;   
         
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
