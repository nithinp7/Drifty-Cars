#version 130
    
const vec2 poissonDisk[9] = vec2[] (
	vec2(0.95581, -0.18159), vec2(0.50147, -0.35807), vec2(0.69607, 0.35559),
	vec2(-0.0036825, -0.59150), vec2(0.15930, 0.089750), vec2(-0.65031, 0.058189),
	vec2(0.11915, 0.78449), vec2(-0.34296, 0.51575), vec2(-0.60380, -0.41527)
);

uniform sampler2D shadowMap;
uniform sampler2D texture;

varying vec4 emissive_;

varying vec4 vertColor;
varying float lightIntensity;

varying vec4 vertTexCoord;

varying vec4 shadowCoord;

float unpackDepth(vec4 color) {
	return color.r + color.g / 255.0;
}

void main(void) {
	vec3 shadowCoordProj = shadowCoord.xyz / shadowCoord.w;
	
	vec4 tex = texture2D(texture, vertTexCoord.st);
	vec4 shadMapCol = texture2D(shadowMap, shadowCoordProj.xy);
	float depth = unpackDepth(shadMapCol);
	vec4 col = vec4(lightIntensity*vertColor.rgb*tex.rgb, vertColor.a*tex.a);
	if(lightIntensity > 0.5) {
		float visibility = 9.0;

		// I used step() instead of branching, should be much faster this way
		for(int n = 0; n < 9; ++n)
			visibility += step(shadowCoordProj.z, unpackDepth(texture2D(shadowMap, shadowCoordProj.xy + poissonDisk[n] / 512.0)));

		gl_FragColor = vec4(col.rgb * min(visibility * 0.05556, lightIntensity), col.a)+emissive_;//+shadMapCol;
	} else
		gl_FragColor = vec4(col.rgb * lightIntensity, col.a)+emissive_;

}