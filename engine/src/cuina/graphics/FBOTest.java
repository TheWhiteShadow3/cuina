package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.util.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
 
//XXX: Diese Klasse hat nichts mit dem Cuina-Projekt zu tun. Ich hab sie nur hier um mir die FBO's Syntax zu übertragen.
public class FBOTest
{
    private int width;
    private int height;
    
    private Texture texture1;
    private Texture texture2;
    private int fbo;
    int phase = 0;
    
    long frames = 0;
    long time = System.currentTimeMillis();
    
    public FBOTest(int width, int height) throws LWJGLException
    {
        this.width = width;
        this.height = height;
        
        Display.setDisplayMode(new DisplayMode(width, height));
        Display.setTitle("FPS: 0");
        Display.create();
        Keyboard.create();
        
        init();
        
        while(true)
        {
            if (Display.isCloseRequested()) break;
//          Display.sync(60);
            
            update();
            
            Display.update();
        }
        Display.destroy();
    }
    
    private void init()
    {
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();   
        glOrtho(0, width, height, 0, -1, 1);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);
        
        try
        {
        	BufferedImage image1 = ResourceManager.loadImage("Gras-Wasser2.png");
            texture1 = TextureLoader.getInstance().getTexture(image1, "Gras-Wasser2.png");
            BufferedImage image2 = ResourceManager.loadImage("title.png");
            texture2 = TextureLoader.getInstance().getTexture(image2, "title.png");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void update()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && phase == 0)
        {
            System.out.println("Rendere FBO 1");
            renderFBO1();
            phase = 1;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_Y) && phase == 1)
        {
            System.out.println("Rendere FBO 2");
            renderFBO2();
            phase = 2;
        }
        
        render();
        
        frames++;
        long now = System.currentTimeMillis();
        if (now - time >= 1000)
        {
            Display.setTitle("FPS: " + frames);
            frames = 0;
            time = now;
        }
    }
    
    private void renderFBO1()
    {
        IntBuffer buffer = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer(); // allocate a 1 int byte buffer
        EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate 
        fbo = buffer.get();
        System.out.println("FBO: " + fbo);
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo );
        EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                        GL11.GL_TEXTURE_2D, texture2.getTextureID(), 0);
        
//		int depthRenderBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
//		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRenderBufferID);
//		EXTFramebufferObject.glRenderbufferStorageEXT(
//				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
//				GL14.GL_DEPTH_COMPONENT32, texture2.getTextureWidth(), texture2.getTextureHeight());
//		EXTFramebufferObject.glFramebufferRenderbufferEXT(
//				EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
//				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
//				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
//				depthRenderBufferID);
        
        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport( 0, 0, texture2.getWidth(), texture2.getHeight());
        
        System.out.println(texture2.getWidth() + ", " +  texture2.getHeight());
//      glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        texture1.bind();
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        
        glPushMatrix();
        glColor3f(1.0f, 1.0f, 1.0f);
        // Texturkoordinaten müssen Vertikal geflippt werden.
        // Vertexkoordinaten entsprechen denen des gesammten Bildschirms. Grund unbekannt.
        glBegin(GL_QUADS);
        {       
            glTexCoord2f(0.0f, 1.0f);
            glVertex2i(0, 0);
            
            glTexCoord2f(0.2f, 1.0f);
            glVertex2i(width / 3, 0);
            
            glTexCoord2f(0.2f, 0.0f);
            glVertex2i(width / 3, height);
            
            glTexCoord2f(0.0f, 0.0f);
            glVertex2i(0, height);
        }
        glEnd();
        
        glPopMatrix();
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
    }
    
    private void renderFBO2()
    {
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo );
        EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                GL_TEXTURE_2D, texture2.getTextureID(), 0);
        
//      glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport( 0, 0, texture2.getWidth(), texture2.getHeight());
        glDisable(GL_TEXTURE_2D);
        
        glPushMatrix();
        
        glColor3f(1.0f, 1.0f, 0.0f);
        glBegin(GL_LINES);
        {
            glVertex2i(0, height-0);
            glVertex2i(width, height-height);
        }
        glEnd();
        glBegin(GL_LINES);
        {
            glVertex2i(width, height-0);
            glVertex2i(0, height-height);
        }
        glEnd();
        
        glPopMatrix();
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
        glEnable(GL_TEXTURE_2D);
    }
    
    private void render()
    {
//      glViewport(0, 0, width, height);
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
        
        texture2.bind();
        
        glPushMatrix();
        
        glColor3f(1.0f, 1.0f, 1.0f);
 
        int x1 = width / 4;
        int x2 = width / 4 * 3;
        int y1 = height / 4;
        int y2 = height / 4 * 3;
        
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.0f, 0.0f);
            glVertex2i(x1, y1);
            
            glTexCoord2f(1.f, 0.0f);
            glVertex2i(x2, y1);
            
            glTexCoord2f(1.f, 1.0f);
            glVertex2i(x2, y2);
            
            glTexCoord2f(0.0f, 1.0f);
            glVertex2i(x1, y2);
        }
        glEnd();
        
        glPopMatrix();
    }
    
    public static void main(String[] args)
    {
    	System.setProperty("org.lwjgl.librarypath",  "G:/Projekte/Java/CuinaEngine/lib/lwjgl/native/windows");
        try
        {
            new FBOTest(640, 640);
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
        }
    }
}