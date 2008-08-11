/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package demos.es1.cube;

import javax.media.opengl.*;
import javax.media.opengl.util.*;
import javax.media.opengl.glu.*;
import java.nio.*;

import com.sun.javafx.newt.*;

public class Cube implements GLEventListener {
    public Cube () {
        this(false, false);
    }

    public Cube (boolean useTexCoords, boolean innerCube) {
        this.innerCube = innerCube;

        // Initialize data Buffers
        this.cubeVertices = BufferUtil.newShortBuffer(s_cubeVertices.length);
        cubeVertices.put(s_cubeVertices);
        cubeVertices.flip();

        this.cubeColors = BufferUtil.newFloatBuffer(s_cubeColors.length);
        cubeColors.put(s_cubeColors);
        cubeColors.flip();

        this.cubeNormals = BufferUtil.newByteBuffer(s_cubeNormals.length);
        cubeNormals.put(s_cubeNormals);
        cubeNormals.flip();

        this.cubeIndices = BufferUtil.newByteBuffer(s_cubeIndices.length);
        cubeIndices.put(s_cubeIndices);
        cubeIndices.flip();
        
        if (useTexCoords) {
            this.cubeTexCoords = BufferUtil.newShortBuffer(s_cubeTexCoords.length);
            cubeTexCoords.put(s_cubeTexCoords);
            cubeTexCoords.flip();
        }
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = GLU.createGLU();
        if(gl.isGLES2()) {
            gl.getGLES2().enableFixedFunctionEmulationMode(GLES2.FIXED_EMULATION_VERTEXCOLORTEXTURE);
            System.err.println("Cubes Fixed emu: FIXED_EMULATION_VERTEXCOLORTEXTURE");
        }
        if(!innerCube) {
            System.err.println("Entering initialization");
            System.err.println("GL Profile: "+GLProfile.getProfile());
            System.err.println("GL:" + gl);
            System.err.println("GL_VERSION=" + gl.glGetString(gl.GL_VERSION));
            System.err.println("GL_EXTENSIONS:");
            System.err.println("  " + gl.glGetString(gl.GL_EXTENSIONS));
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        float aspect = (height != 0) ? ((float)width / (float)height) : 1.0f;

        GL gl = drawable.getGL();
        GL2ES1 glF=null;
        if(gl.isGL2ES1()) {
            glF = drawable.getGL().getGL2ES1();
        }

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glScissor(0, 0, width, height);
        if(innerCube) {
            // Clear background to white
            gl.glClearColor(1.0f, 1.0f, 1.0f, 0.6f);
        } else {
            // Clear background to blue
            gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        }

        if(!innerCube) {
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, light_position, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, zero_vec4, 0);
        gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_SPECULAR, material_spec, 0);

        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_LIGHT0);
        gl.glEnable(gl.GL_COLOR_MATERIAL);
        } else {
        gl.glDisable(gl.GL_LIGHTING);
        gl.glDisable(gl.GL_LIGHT0);
        }
        gl.glEnable(gl.GL_NORMALIZE);
        gl.glEnable(gl.GL_CULL_FACE);

        gl.glShadeModel(gl.GL_SMOOTH);
        gl.glDisable(gl.GL_DITHER);

        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_NORMAL_ARRAY);
        gl.glEnableClientState(gl.GL_COLOR_ARRAY);
        if (cubeTexCoords != null) {
            gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY);
        } else {
            gl.glDisableClientState(gl.GL_TEXTURE_COORD_ARRAY);
        }

        if(null!=glF) {
            glF.glHint(glF.GL_PERSPECTIVE_CORRECTION_HINT, glF.GL_FASTEST);
        }

        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        if(!innerCube) {
            glu.gluPerspective(90.0f, aspect, 1.0f, 100.0f);
        } else {
            gl.glOrthof(-20.0f, 20.0f, -20.0f, 20.0f, 1.0f, 40.0f);
        }
        // weird effect ..: gl.glCullFace(gl.GL_FRONT);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GL2ES1 glF=null;
        if(gl.isGL2ES1()) {
            glF = drawable.getGL().getGL2ES1();
        }

        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.f, 0.f, -30.f);
        gl.glRotatef((float)(time * 29.77f), 1.0f, 2.0f, 0.0f);
        gl.glRotatef((float)(time * 22.311f), -0.1f, 0.0f, -5.0f);

        gl.glVertexPointer(3, gl.GL_SHORT, 0, cubeVertices);
        gl.glColorPointer(4, gl.GL_FLOAT, 0, cubeColors);
        gl.glNormalPointer(gl.GL_BYTE, 0, cubeNormals);
        if (cubeTexCoords != null) {
            gl.glTexCoordPointer(2, gl.GL_SHORT, 0, cubeTexCoords);
            if(null!=glF) {
                glF.glTexEnvi(glF.GL_TEXTURE_ENV, glF.GL_TEXTURE_ENV_MODE, glF.GL_INCR);
            }
        }


        gl.glDrawElements(gl.GL_TRIANGLES, 6 * 6, gl.GL_UNSIGNED_BYTE, cubeIndices);
        // gl.glFinish();

        time += 0.01f;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    static final float[] light_position = { -50.f, 50.f, 50.f, 0.f };
    static final float[] light_ambient = { 0.125f, 0.125f, 0.125f, 1.f };
    static final float[] light_diffuse = { 1.0f, 1.0f, 1.0f, 1.f };
    static final float[] material_spec = { 1.0f, 1.0f, 1.0f, 0.f };
    static final float[] zero_vec4 = { 0.0f, 0.0f, 0.0f, 0.f };

    boolean innerCube;
    boolean initialized = false;
    float time = 0.0f;
    ShortBuffer cubeVertices;
    ShortBuffer cubeTexCoords;
    FloatBuffer cubeColors;
    ByteBuffer cubeNormals;
    ByteBuffer cubeIndices;
    private GLU glu;

    private static final short[] s_cubeVertices =
        {
            -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, 10,
            
            -10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10, -10,
            
            -10, -10, 10, 10, -10, -10, 10, -10, 10, -10, -10, -10,
            
            -10, 10, 10, 10, 10, -10, 10, 10, 10, -10, 10, -10,
            
            10, -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10,
            
            -10, -10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10
        };
    private static final short[] s_cubeTexCoords =
        {
            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,

            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,

            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,

            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,

            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,

            0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,
        };

    private static final float[] s_cubeColors =
        {
            40f/255f, 80f/255f, 160f/255f, 255f/255f, 40f/255f, 80f/255f, 160f/255f, 255f/255f,
            40f/255f, 80f/255f, 160f/255f, 255f/255f, 40f/255f, 80f/255f, 160f/255f, 255f/255f,
            
            40f/255f, 80f/255f, 160f/255f, 255f/255f, 40f/255f, 80f/255f, 160f/255f, 255f/255f,
            40f/255f, 80f/255f, 160f/255f, 255f/255f, 40f/255f, 80f/255f, 160f/255f, 255f/255f,
            
            128f/255f, 128f/255f, 128f/255f, 255f/255f, 128f/255f, 128f/255f, 128f/255f, 255f/255f,
            128f/255f, 128f/255f, 128f/255f, 255f/255f, 128f/255f, 128f/255f, 128f/255f, 255f/255f,
            
            128f/255f, 128f/255f, 128f/255f, 255f/255f, 128f/255f, 128f/255f, 128f/255f, 255f/255f,
            128f/255f, 128f/255f, 128f/255f, 255f/255f, 128f/255f, 128f/255f, 128f/255f, 255f/255f,
            
            255f/255f, 110f/255f, 10f/255f, 255f/255f, 255f/255f, 110f/255f, 10f/255f, 255f/255f,
            255f/255f, 110f/255f, 10f/255f, 255f/255f, 255f/255f, 110f/255f, 10f/255f, 255f/255f,
            
            255f/255f, 70f/255f, 60f/255f, 255f/255f, 255f/255f, 70f/255f, 60f/255f, 255f/255f,
            255f/255f, 70f/255f, 60f/255f, 255f/255f, 255f/255f, 70f/255f, 60f/255f, 255
        };
    private static final byte[] s_cubeIndices =
        {
            0, 3, 1, 2, 0, 1, /* front  */
            6, 5, 4, 5, 7, 4, /* back   */
            8, 11, 9, 10, 8, 9, /* top    */
            15, 12, 13, 12, 14, 13, /* bottom */
            16, 19, 17, 18, 16, 17, /* right  */
            23, 20, 21, 20, 22, 21 /* left   */
        };
    private static final byte[] s_cubeNormals =
        {
            0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,
            
            0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128,
            
            0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0,
            
            0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0,
            
            127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0,
            
            -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0
        };

    private void run(int type) {
        int width = 800;
        int height = 480;
        System.err.println("Cube.run()");
        GLProfile.setProfileGLAny();
        try {
            Window nWindow = null;
            if(0!=(type&USE_AWT)) {
                Display nDisplay = NewtFactory.createDisplay(NewtFactory.AWT, null); // local display
                Screen nScreen  = NewtFactory.createScreen(NewtFactory.AWT, nDisplay, 0); // screen 0
                nWindow = NewtFactory.createWindow(NewtFactory.AWT, nScreen, 0); // dummy VisualID
            }

            GLCapabilities caps = new GLCapabilities();
            // For emulation library, use 16 bpp
            caps.setRedBits(5);
            caps.setGreenBits(6);
            caps.setBlueBits(5);
            caps.setDepthBits(16);
            GLWindow window = GLWindow.create(nWindow, caps);

            window.addGLEventListener(this);

            // Size OpenGL to Video Surface
            window.setSize(width, height);
            window.setFullscreen(true);
            window.setVisible(true);

            long curTime;
            long startTime = System.currentTimeMillis();
            while (((curTime = System.currentTimeMillis()) - startTime) < 20000) {
                window.display();
            }

            // Shut things down cooperatively
            window.close();
            window.getFactory().shutdown();
            System.out.println("Cube shut down cleanly.");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static int USE_NEWT      = 0;
    public static int USE_AWT       = 1 << 0;

    public static void main(String[] args) {
        int type = USE_NEWT ;
        for(int i=args.length-1; i>=0; i--) {
            if(args[i].equals("-awt")) {
                type |= USE_AWT; 
            }
        }
        new Cube().run(type);
        System.exit(0);
    }
}
