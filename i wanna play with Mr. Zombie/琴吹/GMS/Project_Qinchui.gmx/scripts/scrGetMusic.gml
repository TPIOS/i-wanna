///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
        roomSong = -1;              //play nothing
        break;
    case rQCstage01:
    case rQCstage02:
    case rQCstage04:
    case rQCstage05:
    //case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
        roomSong = musQCstageBgm;
        break;                      //make sure to always put a break after setting the song
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    case rQCPenalty:
    case rQCPenalty2:
    case rQCstage03:
        roomSong = musQCpenaltyBgm;
        break;
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;
    case rEnd:
        roomSong = -1;              //play nothing
        break;
    default:                        //default option in case the room does not have a song set
        scrStopMusic();
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
