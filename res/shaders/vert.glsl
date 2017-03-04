#version 130
    
uniform mat4 modelview;
uniform mat4 transform;
uniform mat3 normalMatrix;

uniform vec4 lightPosition;
uniform vec3 lightDirection;

uniform mat4 texMatrix;

uniform mat4 shadowTransform;

attribute vec4 emissive;
attribute vec2 texCoord;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;

varying vec4 vertColor;
varying float lightIntensity;

varying vec4 vertTexCoord;
varying vec4 emissive_;

varying vec4 shadowCoord;

void main() {
	gl_Position = transform*position;
	vec4 ecPos = modelview*position;
	
	vertColor = color;
	vec3 vertNorm = normalize(normalMatrix*normal);
	
	vec3 dif = lightPosition.xyz - ecPos.xyz;
	
	vec3 difN = normalize(dif);
	float distSq = dot(dif, dif);
	
	lightIntensity = (1-smoothstep(dot(lightDirection, difN), 1.1, 0.7))*(0.5 * dot(-lightDirection, vertNorm) + 0.5)/(1+pow(4, .00001*distSq - 2));
	  
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	
	emissive_ = emissive;
	
	shadowCoord = shadowTransform * (ecPos + vec4(-vertNorm, 0.0));
}