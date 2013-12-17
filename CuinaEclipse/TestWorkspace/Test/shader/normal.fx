uniform sampler2D colorTexture;
uniform sampler2D normalTexture;
uniform sampler2D specularTexture;
uniform vec3 camPos;

// New bumpmapping
varying vec3 faceNormal;
varying vec3 position;

const float MAX_DIST = 200.0;

void main()
{
	vec4 specular = vec4(0.0);
	vec4 diffuse;
	vec4 color = texture2D (colorTexture, gl_TexCoord[0].st);
	vec3 normal = texture2D (normalTexture, gl_TexCoord[0].st).rgb;
	float specValue = texture2D (specularTexture, gl_TexCoord[0].st).x;
	
	if (color.a == 0.0)
	{
		gl_FragColor = vec4(0.0);
		return;
	}
	
	vec3 lightVec = gl_LightSource[0].position.xyz - position;
	float dist = length(lightVec);
	lightVec = normalize(lightVec);
	float attenuation = max(1.0 - (dist / MAX_DIST), 0.0);/*(gl_LightSource[0].constantAttenuation + 
							   gl_LightSource[0].linearAttenuation * dist + 
							   gl_LightSource[0].quadraticAttenuation * dist * dist);*/
				   
	float fac = 1.0;
	if (normal != vec3(0.0))
	{
		normal = normalize(2.0 * normal - 1.0);
		fac = max (dot (lightVec, normal), 0.0);

		if (specValue != 0.0 && fac != 0.0)
		{
			vec3 cameraVec = normalize(gl_LightSource[1].position.xyz - position);
			
			vec3 halfVec = normalize(lightVec + cameraVec);
			float nxHalf = max(0.0, dot(normal, halfVec));
			float specularPower = pow(nxHalf , specValue * 10.0); 
			specular = vec4(gl_LightSource[0].specular * specularPower * attenuation);
		}
	}
	diffuse = gl_LightSource[0].diffuse * fac * attenuation;
	/*if (lightDist > MAX_DIST)
	{
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
		return;
	}*/

	//color = vec4(lightVec, 1);
	gl_FragColor = gl_LightSource[0].ambient + (diffuse * color) + (specular * color.a);
}			
