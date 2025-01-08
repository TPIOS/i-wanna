///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
        roomSong = musStage01;
        break;
    case rs1:
    case rs2:
    case rs3:
    case rs4:
    case rs5:
        roomSong = musStage01;
        break;
    case rs6:
    case rs7:
    case rs8:
    case rs9:
    case rs10:
        roomSong = musStage02;
        break;
    case rs11:
    case rs12:
    case rs13:
    case rs14:
    case rs15:
        roomSong = musStage03;
        break;
    //case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
/*        roomSong = musGuyRock;
        break;                      //make sure to always put a break after setting the song
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;*/
    case rEnd:
        roomSong = musEnd;              
        break;
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
