uniform sampler2D normalTexture;

// New bumpmapping
varying vec3 faceNormal;
varying vec3 vertexNormal;
varying float lightDist;
varying vec3 lightVec;
varying vec3 halfVec;
varying vec3 eyeVec;

void main()
{
	// lookup normal from normal map, move from [0,1] to  [-1, 1] range, normalize
	vec3 normal = 2.0 * texture2D (normalTexture, gl_TexCoord[0].st).rgb - 1.0;
	normal = normalize (normal);
	//normal = dot(faceNormal, normal);
	// compute diffuse lighting
	
	vec3 nn = vec3(0, 1, 0);
	vec3 ll = vec3(8, 8, 8);
	float lamberFactor = max (dot (lightVec, normal), 0.0) ;
	vec3 diffuseMaterial = vec3(0.5);
	//vec4 diffuseLight = vec4(0.0);
	
	// compute specular lighting
	vec4 specularMaterial ;
	vec4 specularLight ;
	float shininess ;
  
	// compute ambient
	float lightFac = 200.0 / (lightDist * lightDist);
	//gl_FragColor = vec4(-lightVec, 1);
	gl_FragColor = vec4(diffuseMaterial * lamberFactor, 1);// + gl_LightSource[0].ambient;	
}			
