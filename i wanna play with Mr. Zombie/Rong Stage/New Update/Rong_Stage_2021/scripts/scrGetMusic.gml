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
    /*
    case rRong:
        if(global.secretItem[GM]) roomSong = musRong4;
        else if(instance_exists(objPlayer)){
            if(objPlayer.x<800 and objPlayer.y>608*2) roomSong = musRong4;
        }
        else if(!global.secretItem[BANSHOOT] and !global.secretItem[BOMB]) roomSong = musRong1;
        else if (global.secretItem[BANSHOOT]) roomSong = musRong2;
        else if (!global.secretItem[BANSHOOT] and global.secretItem[BOMB]) roomSong = musRong2;
        break;
    */
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
