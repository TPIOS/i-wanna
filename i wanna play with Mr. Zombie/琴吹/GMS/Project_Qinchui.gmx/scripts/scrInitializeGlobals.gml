///initializes all global variables needed for the game

scrSetGlobalOptions();       //initialize global game options

global.savenum = 1;
global.difficulty = 0;  //0 = medium, 1 = hard, 2 = very hard, 3 = impossible
global.death = 0;
global.time = 0;
global.timeMicro = 0;
global.saveRoom = 0;
global.savePlayerX = 0;
global.savePlayerY = 0;
global.grav = 1;
global.saveGrav = 1;

var i;
for (i = global.secretItemTotal-1; i >= 0; i-=1)
{
    global.secretItem[i] = false;
    global.saveSecretItem[i] = false;
}

var i;
for (i = global.bossItemTotal-1; i >= 0; i-=1)
{
    global.bossItem[i] = false;
    global.saveBossItem[i] = false;
}

global.gameClear = false;
global.saveGameClear = false;

for (i = 99; i >= 0; i-=1)
{
    global.trigger[i] = false;
}

global.gameStarted = false;     //determines whether the game is in progress (enables saving, restarting, etc.)
global.noPause = false;         //sets whether or not to allow pausing (useful for bosses to prevent desync)
global.autosave = false;        //keeps track of whether or not to autosave the next time the player is created
global.noDeath = false;         //keeps track of whether to give the player god mode
global.infJump = false;         //keeps track of whether to give the player infinite jump

global.gamePaused = false;      //keeps track of whether the game is paused or not
global.pauseSurf = -1;       //stores the screen surface when the game is paused
global.pauseDelay = 0;      //sets pause delay so that the player can't quickly pause buffer

global.currentMusicID = -1;  //keeps track of what song the current music is
global.currentMusic = -1;    //keeps track of current main music instance
global.deathSound = -1;     //keeps track of death sound when the player dies
global.gameOverMusic = -1;   //keeps track of game over music instance
global.musicFading = false;     //keeps track of whether the music is being currently faded out
global.currentGain = 0;     //keeps track of current track gain when a song is being faded out

global.menuSelectPrev[0] = 0;     //keeps track of the previously selected option when navigating away from the difficulty menu
global.menuSelectPrev[1] = 0;     //keeps track of the previously selected option when navigating away from the options menu

//get the default window size
global.windowWidth = view_wview[0];
global.windowHeight = view_hview[0];

//get savedata folder
if (global.sandboxEnable) //in sandbox mode, get appdata folder
{
    //get appdata folder
    var index, str;
    str = "";
    for (index = string_length(temp_directory); index != 0; index -= 1)
    {
        str = string_insert(string_char_at(temp_directory, index), str, 0);
        if (string_pos("\Local", str) != 0)
        {
            break;
        }
    }
    if (global.sandboxMode == 0)
    {
        global.dataFolder = string_copy(temp_directory, 0, index) + "Local\" + global.sandboxName + "\Data\";
    }
    else if (global.sandboxMode == 1)
    {
        global.dataFolder = string_copy(temp_directory, 0, index) + "Roaming\" + global.sandboxName + "\Data\";
    }
}
else //not in sandbox mode, get working directory
{
    global.dataFolder = working_directory + "\Data\";
}
// create data folder
if (!directory_exists(global.dataFolder))

randomize();    //make sure the game starts with a random seed for RNG


