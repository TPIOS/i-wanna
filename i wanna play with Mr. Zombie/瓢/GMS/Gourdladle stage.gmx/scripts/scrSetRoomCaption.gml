///sets the room caption

var roomCaption;
roomCaption = global.roomCaptionDef;

if (global.gameStarted)
{
    roomCaption += " -"
    roomCaption += " Deaths: " + string(global.death);
    roomCaption += " Time: ";
    
    var t;
    t = floor(global.time);
    
    roomCaption += string(t div 3600) + ":";
    t = t mod 3600;
    roomCaption += string(t div 600);
    t = t mod 600;
    roomCaption += string(t div 60) + ":";
    t = t mod 60;
    roomCaption += string(t div 10);
    t = t mod 10;
    roomCaption += string(t);
}

if (room_caption != global.roomCaptionLast)  //only update the caption when it changes
    room_caption = roomCaption;

global.roomCaptionLast = roomCaption;

