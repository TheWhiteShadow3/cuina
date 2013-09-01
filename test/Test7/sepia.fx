// scene texture
uniform sampler2D Texture;
 
void main(void)
{    
	vec4 Color = texture2D(Texture, vec2(gl_TexCoord[0]));
	if (Color.a == 0) discard;
	
	// Sepia colors                  
	vec3 Sepia1 = vec3( 0.2, 0.05, 0.2 );
	vec3 Sepia2 = vec3( 1.0, 0.9, 0.5 );
 
 
	float SepiaMix = dot(vec3(0.3, 0.59, 0.11), vec3(Color));
	//Color = mix(Color, vec4(SepiaMix), vec4(0.5));
	vec3 Sepia = mix(Sepia1, Sepia2, SepiaMix);
	 
	gl_FragColor = vec4(Sepia, Color.a);
}