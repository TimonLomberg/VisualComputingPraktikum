#version 430 core

out vec4 FragColor;

const vec3 objectColor = vec3(.1, .1, .8);
const vec3 lightColor = vec3(.9, .9, .9 );

void main(void) {
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    vec3 result = ambient * objectColor;
    FragColor = vec4(result, 1.0);
   // FragColor = vec4(0.7, 0.9, 0.6, 1.0);
}
