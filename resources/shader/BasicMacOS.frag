#version 410 core
#extension GL_ARB_explicit_uniform_location : enable

in vec4 vColor;
out vec4 FragColor;


void main (void)
{
//    FragColor = vec4(0.9, 0.8, 0.6, 1.0);
	FragColor = vColor;
}
