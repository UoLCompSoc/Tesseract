/* This shader is derived from shaders on shadertoy.com, which supply no licesning information and thus fall under the below license:
 * This shader is licensed under the creative commons Attribution-NonCommercial-ShareAlike 3.0 Unported license.
 *
 * For more details see the LICENSE file.
 * The original shaders can be found at
 * https://www.shadertoy.com/view/Xdl3WH
 * https://www.shadertoy.com/view/MdXGzr
 */

#ifdef GL_ES 
    #define LOWP lowp 
    precision mediump float; 
#else 
    #define LOWP  
#endif

#define PI 3.1416
#define WAVE_SIZE 0.5
#define SPEED 4.0

varying LOWP vec4 v_color; 
varying vec2 v_texCoords; 

uniform vec2 iResolution;
uniform sampler2D u_texture;
uniform float vortexFlag;
uniform float transitionTime;
uniform float expectedTransitionTime;

void main() {
    if(vortexFlag > 0.5) {
    /*
        vec2 p = (2.0 * gl_FragCoord.xy / iResolution.xy - 1.0)
        * vec2(iResolution.x / iResolution.y, 1.0);
        
        vec2 uv = vec2(atan(p.y, p.x) * 1.0/PI, 1.0 / sqrt(dot(p, p))) * vec2(2.0, 1.0);
        
        uv.x += sin(2.0 * uv.y + transitionTime * 60.0);
        
        vec3 c = texture2D(u_texture, uv).xyz / (uv.y * 0.5 + 1.0);
        
        float mod = transitionTime * 20.0;
    
        gl_FragColor = vec4(c/mod, 1.0);
        */
    vec2 rcpResolution = 1.0 / iResolution.xy;
	vec2 uv = gl_FragCoord.xy * rcpResolution;
	
	vec4 mouseNDC = -1.0 + vec4(0.5, 0.5, uv) * 2.0;
	vec2 diff     = mouseNDC.zw - mouseNDC.xy;
	
	float dist  = length(diff);
	float angle = PI * dist * WAVE_SIZE + transitionTime * SPEED;
	 
	vec3 sincos;
	sincos.x = sin(angle);
	sincos.y = cos(angle);
	sincos.z = -sincos.x;
	
	vec2 newUV;
	mouseNDC.zw -= mouseNDC.xy;
	newUV.x = dot(mouseNDC.zw, sincos.yz);
	newUV.y = dot(mouseNDC.zw, sincos.xy);
	
	vec3 col = texture2D( u_texture, newUV.xy ).xyz;
    
    //float mod = (newUV.y * 0.5 + 1.0) * 1.0/(transitionTime/expectedTransitionTime);
    
    float mod = clamp(iResolution.x/gl_FragCoord.x + iResolution.y/gl_FragCoord.y ,0.01, 0.5);
	
	gl_FragColor = vec4(col*mod, 1);
    } else {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    }
};