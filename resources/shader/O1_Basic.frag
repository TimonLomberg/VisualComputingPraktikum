#version 430 core

in vec4 vColor;
out vec4 FragColor;

void main (void)
{
//    FragColor = vec4(0.7, 0.9, 0.6, 1.0);
	FragColor = vColor;
}
