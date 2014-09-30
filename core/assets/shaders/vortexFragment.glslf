#version 120

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
    #define MEDP mediump
    #define HIGHP highp
    precision mediump float;
#else
    #define MEDP
    #define LOWP
    #define HIGHP
#endif

#define PI 3.1416
#define WAVE_SIZE 0.5
#define SPEED 10.0

varying vec4 v_color; 
varying vec2 v_texCoords; 

uniform mat4 u_projTrans; 
uniform vec2 iResolution;
uniform sampler2D u_texture;
uniform float vortexFlag;
uniform float transitionTime;

void main() {
    if(vortexFlag > 0.5) {
        vec2 rcpResolution = 1.0 / iResolution.xy;
        vec2 uv = gl_FragCoord.xy * rcpResolution;
        
        vec4 mouseNDC = vec4(-1.0) + vec4(0.5, 0.5, uv) * 2.0;
        vec2 diff     = mouseNDC.zw - mouseNDC.xy;
        
        float dist  = length(diff);
        float angle = PI * dist * WAVE_SIZE + transitionTime * SPEED;
         
        vec3 sincos = vec3(sin(angle), cos(angle), 0.0);
        sincos.z = -sincos.x;
        
        vec2 newUV = vec2(0.0, 0.0);
        mouseNDC.zw -= mouseNDC.xy;
        newUV.x = dot(mouseNDC.zw, sincos.yz);
        newUV.y = dot(mouseNDC.zw, sincos.xy);
        
        vec3 col = texture2D( u_texture, newUV.xy ).xyz;
        
        float mod = clamp(iResolution.x/gl_FragCoord.x + iResolution.y/gl_FragCoord.y , 0.01, 0.5);
        
        gl_FragColor = vec4(col*mod, 1.0);
    } else {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    }
};