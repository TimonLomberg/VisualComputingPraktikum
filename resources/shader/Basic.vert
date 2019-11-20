#version 430 core
// Basic Vertex Shader that performs geometric transformations

// Author: Karsten Lehn
// Version: 7.10.2018

// User definied in variables
// position and color of vertex
layout (location = 0) in vec3 vposition;
layout (location = 1) in vec3 vInColor;
// Definition of uniforms
// Projection and model-view matrix
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;

// User definied out variable
// Color of the vertex
out vec4 vColor;

void main(void) {
    // Calculation of the model-view-perspective transform
	gl_Position = pMatrix * mvMatrix * vec4(vposition, 1.0);
	// The color information is forwarded
	// to the next pipeline stage
	vColor = vec4(vInColor, 1.0);
}
