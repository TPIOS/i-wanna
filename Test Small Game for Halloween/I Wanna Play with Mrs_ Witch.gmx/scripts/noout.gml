var a;


//top left towards down
a=instance_create(-32,-32,objBlock)
a.image_yscale=room_height/32+1
//top left towards right
a=instance_create(-32,-32,objBlock)
a.image_xscale=room_width/32+1

//bottom right towards up
a=instance_create(room_width,room_height,objBlock)
a.image_yscale=-room_height/32-1
