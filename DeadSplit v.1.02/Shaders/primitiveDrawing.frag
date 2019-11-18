#version 110

uniform vec4 LowColor;
uniform vec4 HighColor;

varying vec2 fragTexCoord;

void main()
{
	vec4 color = mix( LowColor, HighColor, fragTexCoord.x );
	gl_FragColor = color; 
}