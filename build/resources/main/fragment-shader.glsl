#version 330 core

out vec4 FragColor;

//in vec3 outColor;
in vec2 outTex;

uniform sampler2D ourTex;

void main()
{
    FragColor = texture(ourTex, outTex);
}
