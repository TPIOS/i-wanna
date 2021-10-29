///sets the room caption

//var roomCaption = global.roomCaptionDef;

var roomCaption = "";

if (global.gameStarted)
{
    roomCaption += " -"
    roomCaption += " Deaths: " + string(global.death);
    roomCaption += " Time: ";
    
    var t = floor(global.time);
    
    roomCaption += string(t div 3600) + ":";
    t = t mod 3600;
    roomCaption += string(t div 600);
    t = t mod 600;
    roomCaption += string(t div 60) + ":";
    t = t mod 60;
    roomCaption += string(t div 10);
    t = t mod 10;
    roomCaption += string(t);
    
    /*
    if(instance_exists(objDayNight)){
        roomCaption = roomCaption + " hour: " + string(floor(objDayNight.time / 120));
        roomCaption = roomCaption + " minute: " + string(floor((objDayNight.time % 120) / 2));
        roomCaption = roomCaption + " day: " + string(objDayNight.day);
        roomCaption = roomCaption + " night: " + string(objDayNight.night);
        roomCaption = roomCaption + " night: " + string(objDayNight.dayNight);
    }
    */
    
    if(instance_exists(objDayNight)){
        roomCaption = roomCaption + " hour: " + string(floor(global.dayNight / 120));
        roomCaption = roomCaption + " minute: " + string(floor((global.dayNight % 120) / 2));
        roomCaption = roomCaption + " day: " + string(objDayNight.day);
        roomCaption = roomCaption + " night: " + string(objDayNight.night);
        roomCaption = roomCaption + " dayNight: " + string(objDayNight.dayNight);
        
        //roomCaption = roomCaption + " ghost: " + string(objWGhost.shadow);
        //roomCaption = roomCaption + " hspeed: " + string(objWGhost.hspeed);
        //roomCaption = roomCaption + " spd: " + string(objWGhost.spd);
        
        roomCaption = roomCaption + " open: " + string(objDoor.open);
        roomCaption = roomCaption + " close: " + string(objDoor.close);
        roomCaption = roomCaption + " angle: " + string(objDoor.angle);
    }
    
}

if (roomCaption != global.roomCaptionLast)  //only update the caption when it changes
    window_set_caption(roomCaption);

global.roomCaptionLast = roomCaption;
