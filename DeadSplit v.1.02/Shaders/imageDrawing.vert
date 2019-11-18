#version 110

uniform mat3 ModelToClip;
uniform float ZOrder;

attribute vec4 position;
varying vec2 fragTexCoord;

void main()
{
	vec3 screenPos = ModelToClip * vec3( position.xy, 1 );
	gl_Position = vec4( screenPos.xy, ZOrder, 1 );
	fragTexCoord = position.zw; 
}