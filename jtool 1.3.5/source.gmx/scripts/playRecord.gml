global.record = 2;
with oRecordPlayer {instance_destroy();}
instance_create(global.recordX,global.recordY,oRecordPlayer);
oRecordPlayer.vspeed = global.recordVspeed
oRecordPlayer.djump = global.recordDjump
