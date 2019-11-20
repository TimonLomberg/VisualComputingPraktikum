#version 430 core
// Basic Vertex Shader that performs geometric transformations

// Author: Karsten Lehn
// Version: 7.10.2018

// User defined in variable
// Color from vertex shader
in vec4 vColor;
// User defined out variable, fragment color
out vec4 FragColor;

void main (void)
{
    // The input fragment color is forwarded
    // to the next pipeline stage
	FragColor = vColor;
}
