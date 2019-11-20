#version 410 core
#extension GL_ARB_explicit_uniform_location : enable

// position and color of vertex as input vertex attribute
layout (location = 0) in vec3 vposition;
layout (location = 1) in vec3 vInColor;
// Projection and model-view matrix as input uniform variables
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;

// Color of the vertex as output of the vertex shader
out vec4 vColor;

void main(void) {
    // Calculation of the model-view-perspective transform
	gl_Position = pMatrix * mvMatrix * vec4(vposition, 1.0);
	// The color information is forwaarded to the next stage of the pipline
	vColor = vec4(vInColor, 1.0);
}
