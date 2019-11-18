#version 110

uniform sampler2D SpriteImage;
varying vec2 fragTexCoord;

void main()
{
	gl_FragColor = texture2D( SpriteImage, fragTexCoord ); 
}