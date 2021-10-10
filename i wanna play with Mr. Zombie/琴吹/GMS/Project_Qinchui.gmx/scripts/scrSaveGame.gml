///scrSaveGame(saveposition)
///saves the game
///argument0 - sets whether the game should save the player's current location or just save the deaths/time

var savePosition;
savePosition = argument0;

//save the player's current location variables if the script is currently set to (we don't want to save the player's location if we're just updating death/time)
if (savePosition)
{    
    global.saveRoom = room_get_name(room);
    global.savePlayerX = objPlayer.x;    
    global.savePlayerY = objPlayer.y;
    global.saveGrav = global.grav;
    
    //check if player is saving inside of a wall or in the ceiling when the player's position is floored to prevent save locking
    with (objPlayer)
    {
        if (!place_free(floor(global.savePlayerX),global.savePlayerY))
        {
            global.savePlayerX += 1;
        }
        
        if (!place_free(global.savePlayerX,floor(global.savePlayerY)))
        {
            global.savePlayerY += 1;
        }
        
        if (!place_free(floor(global.savePlayerX),floor(global.savePlayerY)))
        {
            global.savePlayerX += 1;
            global.savePlayerY += 1;
        }
    }
    
    //floor player position to match standard engine behavior
    global.savePlayerX = floor(global.savePlayerX);
    global.savePlayerY = floor(global.savePlayerY);
    
    var i;
    for (i = 0; i < global.secretItemTotal; i += 1)
    {
        global.saveSecretItem[i] = global.secretItem[i];
    }
    
    var i;
    for (i = 0; i < global.bossItemTotal; i += 1)
    {
        global.saveBossItem[i] = global.bossItem[i];
    }
    
    global.saveGameClear = global.gameClear;
}

//create a map for save data
var saveMap;
saveMap = jso_new_map();

jso_map_add_real(saveMap,"death",global.death);
jso_map_add_real(saveMap,"time",global.time);
jso_map_add_real(saveMap,"timeMicro",global.timeMicro);

jso_map_add_real(saveMap,"difficulty",global.difficulty);
jso_map_add_string(saveMap,"saveRoom",global.saveRoom);
jso_map_add_real(saveMap,"savePlayerX",global.savePlayerX);
jso_map_add_real(saveMap,"savePlayerY",global.savePlayerY);
jso_map_add_real(saveMap,"saveGrav",global.saveGrav);

var i;
for (i = 0; i < global.secretItemTotal; i += 1)
{
    jso_map_add_real(saveMap,"saveSecretItem["+string(i)+"]",global.saveSecretItem[i]);
}

var i;
for (i = 0; i < global.bossItemTotal; i += 1)
{
    jso_map_add_real(saveMap,"saveBossItem["+string(i)+"]",global.saveBossItem[i]);
}

jso_map_add_real(saveMap,"saveGameClear",global.saveGameClear);

//add md5 hash to verify saves and make them harder to hack
jso_map_add_string(saveMap,"mapMd5",md5string(jso_encode_map(saveMap)+global.md5StrAdd));

//use text file

//open the save file

f = file_text_open_write(scrGetDataFolder()+"save"+string(global.savenum));

//write map to the save file with base64 encoding
file_text_write_string(f,base64_encode(jso_encode_map(saveMap)));

file_text_close(f);

//destroy the map
jso_cleanup_map(saveMap);
