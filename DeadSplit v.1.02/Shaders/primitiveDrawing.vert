#version 110

attribute vec2 position;

uniform mat3 ModelToClip;
uniform float ZOrder;

void main()
{
	vec3 screenPos = ModelToClip * vec3( position, 1 );
	gl_Position = vec4( screenPos.xy, ZOrder, 1 );
}