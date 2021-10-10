///scrLoadGame(loadfile)
///loads the game
///argument0 - sets whether or not to read the save file when loading the game

var loadFile;
loadFile = argument0;

//only load save data from the save file if the script is currently set to (we should only need to load these on first load because the game stores them afterwards)
if (loadFile)
{       
    //load the save map
            
    //use text file
    
    var saveMap;
    
    var f;
    f = file_text_open_read(scrGetDataFolder()+"save"+string(global.savenum));
    
    saveMap = jso_decode_map(base64_decode(file_text_read_string(f)));
    
    file_text_close(f);
       
    var saveValid;    
    saveValid = true;   //keeps track of whether or not the save being loaded is valid
    
    if (saveMap != -1)  //check if the save map loaded correctly
    {
        global.death = jso_map_get(saveMap,"death");
        global.time = jso_map_get(saveMap,"time");
        global.timeMicro = jso_map_get(saveMap,"timeMicro");
        
        global.difficulty = jso_map_get(saveMap,"difficulty");
        
        global.saveRoom = jso_map_get(saveMap,"saveRoom");
        
        global.savePlayerX = jso_map_get(saveMap,"savePlayerX");
        global.savePlayerY = jso_map_get(saveMap,"savePlayerY");
        global.saveGrav = jso_map_get(saveMap,"saveGrav");
        
        if (is_string(global.saveRoom))
        {
            variable_local_set(global.saveRoom, -1); //used to determine whether the resource name exists
            saveValid = execute_string("if (" + global.saveRoom + " != -1) { if (!room_exists(" + global.saveRoom + ")) return false; else return true; } else { return false; }");  //check if the room index in the save is valid
        }
        else
        {
            saveValid = false;
        }
        for (i = 0; i < global.secretItemTotal; i += 1)
        {
            global.saveSecretItem[i] = jso_map_get(saveMap,"saveSecretItem["+string(i)+"]");
        }
        
        for (i = 0; i < global.bossItemTotal; i += 1)
        {
            global.saveBossItem[i] = jso_map_get(saveMap,"saveBossItem["+string(i)+"]");
        }
        
        global.saveGameClear = jso_map_get(saveMap,"saveGameClear");
        
        //load md5 string from the save map
        var mapMd5;
        mapMd5 = jso_map_get(saveMap,"mapMd5");
        
        //check if md5 is not a string in case the save was messed with or got corrupted
        if (!is_string(mapMd5))
            mapMd5 = "";   //make it a string for the md5 comparison
        
        //generate md5 string to compare with
        ds_map_delete(saveMap,"mapMd5");
        var genMd5;
        genMd5 = md5string(jso_encode_map(saveMap)+global.md5StrAdd);
        
        if (mapMd5 != genMd5)   //check if md5 hash is invalid
            saveValid = false;
          
        //destroy the map
        jso_cleanup_map(saveMap);
    }
    else
    {
        //save map didn't load correctly, set the save to invalid
        saveValid = false;
    }
    
    if (!saveValid) //check if the save is invalid
    {
        //save is invalid, restart the game
        
        show_message("Save invalid!");
        
        scrRestartGame();
        
        exit;
    }
}

//set game variables and set the player's position

with (objPlayer) //destroy player if it exists
    instance_destroy();

global.gameStarted = true;  //sets game in progress (enables saving, restarting, etc.)
global.noPause = false;     //disable no pause mode
global.autosave = false;    //disable autosaving since we're loading the game

global.grav = global.saveGrav;

var i;
for (i = 0; i < global.secretItemTotal; i += 1)
{
    global.secretItem[i] = global.saveSecretItem[i];
}

var i;
for (i = 0; i < global.bossItemTotal; i += 1)
{
    global.bossItem[i] = global.saveBossItem[i];
}

global.gameClear = global.saveGameClear;

instance_create(global.savePlayerX,global.savePlayerY,objPlayer);

execute_string("room_goto("+global.saveRoom+");");
