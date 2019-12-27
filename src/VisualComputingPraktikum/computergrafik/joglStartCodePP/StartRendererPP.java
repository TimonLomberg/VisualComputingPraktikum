package VisualComputingPraktikum.computergrafik.joglStartCodePP; /**
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

import java.nio.FloatBuffer;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;

/**
 * Performs the OpenGL rendering
 * Uses the programme pipeline commands in the core profile only.
 * Thus, a vertex and fragment shader is used.
 *
 * Rotation and translation of the camera is included.
 * 	    Use keyboard: left/right/up/down-keys and +/-Keys
 * 	    Alternatively use mouse movements:
 * 	        press left/right button and move and use mouse wheel
 *
 * Draws a simple triangle.
 * Serves as a template (start code) for setting up an OpenGL/Jogl application
 * which is using a vertex and fragment shader in the core profile.
 *
 * Please make sure setting the file path and names of the shaders correctly (see below).
 *
 * Based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn
 * @version 3.9.2015, 15.9.2015, 18.9.2015, 10.9.2017, 2.10.2018, 7.10.2018
 *
 */
public class StartRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;
    // Defining shader source code file paths and names
    final String shaderPath = ".\\resources\\shader\\";
    final String vertexShaderFileName = "Basic.vert";
    final String fragmentShaderFileName = "Basic.frag";
//    final String vertexShaderFileName = "BasicMacOS.vert";
//    final String fragmentShaderFileName = "BasicMacOS.frag";

    // Object for loading shaders and creating a shader program
    private ShaderProgram shaderProgram;

    // OpenGL buffer names for data allocation and handling on GPU
    int[] vaoName;  // List of names (integer pointers) of vertex array objects
    int[] vboName;  // List of names (integer pointers) of vertex buffer objects

    // Declaration of an object for handling keyboard and mouse interactions
    InteractionHandler interactionHandler;

    // Declaration for using the projection-model-view matrix tool
    PMVMatrix pmvMatrix;

    /**
     * Standard constructor for object creation.
     */
    public StartRendererPP() {
        // Create the OpenGL canvas with default capabilities
        super();
        // Add this object as OpenGL event listener to the canvas
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Create the canvas with the requested OpenGL capabilities
     * @param capabilities The capabilities of the canvas, including the OpenGL profile
     */
    public StartRendererPP(GLCapabilities capabilities) {
        // Create the OpenGL canvas with the requested OpenGL capabilities
        super(capabilities);
        // Add this object as an OpenGL event listener to the canvas
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Helper method for creating an interaction handler object and registering it
     * for key press and mouse interaction call backs.
     */
    private void createAndRegisterInteractionHandler() {
        // The constructor call of the interaction handler generates meaningful default values.
        // The start parameters can also be set via setters
        // (see class definition of the interaction handler).
        interactionHandler = new InteractionHandler();
        this.addKeyListener(interactionHandler);
        this.addMouseListener(interactionHandler);
        this.addMouseMotionListener(interactionHandler);
        this.addMouseWheelListener(interactionHandler);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * that is called when the OpenGL renderer is started for the first time.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        // Outputs information about the available and chosen profile
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Loading the vertex and fragment shaders and creation of the shader program.
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath,
                vertexShaderFileName, fragmentShaderFileName);

        // Create object for projection-model-view matrix calculation.
        pmvMatrix = new PMVMatrix();

        // Vertices for drawing a triangle.
        // To be transferred to a vertex buffer object on the GPU.
        // Interleaved data layout: position, color
        float[] verticies = {
                -0.5f,  0.5f,  +0.0f, 	 // 0 position
                 0.0f,  0.68f,  0.85f,  // 0 color
                 0.0f, -0.5f,   0.0f, 	 // 1 position
                 0.0f,  0.68f,  0.85f,  // 1 color
                 0.5f,  0.5f,   0.0f, 	 // 2 position
                 0.0f,  0.68f,  0.85f,  // 2 color
        };

        // Create and activate a vertex array object (VAO)
        // Useful for switching between data sets for object rendering.
        vaoName = new int[1];
        // Creating the buffer on GPU.
        gl.glGenVertexArrays(1, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO) on GPU.");
        // Switch to this VAO.
        gl.glBindVertexArray(vaoName[0]);

        // Create, activate and initialize vertex buffer object (VBO)
        // Used to store vertex data on the GPU.
        vboName = new int[1];
        // Creating the buffer on GPU.
        gl.glGenBuffers(1, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO) on GPU.");
        // Activating this buffer as vertex buffer object.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);
        // Transferring the vertex data (see above) to the VBO on GPU.
        // (floats use 4 bytes in Java)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticies.length * Float.BYTES,
                FloatBuffer.wrap(verticies), GL.GL_STATIC_DRAW);

        // Activate and map input for the vertex shader from VBO,
        // taking care of interleaved layout of vertex data (position and color),
        // Enable layout position 0
        gl.glEnableVertexAttribArray(0);
        // Map layout position 0 to the position information per vertex in the VBO.
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6* Float.BYTES, 0);
        // Enable layout position 1
        gl.glEnableVertexAttribArray(1);
        // Map layout position 1 to the color information per vertex in the VBO.
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6* Float.BYTES, 3* Float.BYTES);

        // Set start parameter(s) for the interaction handler.
        interactionHandler.setEyeZ(2);

        // Switch on back face culling
        // gl.glEnable(GL.GL_CULL_FACE);
        //gl.glCullFace(GL.GL_BACK);

        // Switch on depth test.
        gl.glEnable(GL.GL_DEPTH_TEST);

        // Set background color of the GLCanvas.
        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called by the OpenGL animator for every frame.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        // Clear color and depth buffer
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Controlling the interaction settings
/*        System.out.println("Camera: z = " + interactionHandler.getEyeZ() + ", " +
                "x-Rot: " + interactionHandler.getAngleXaxis() +
                ", y-Rot: " + interactionHandler.getAngleYaxis() +
                ", x-Translation: " + interactionHandler.getxPosition()+
                ", y-Translation: " + interactionHandler.getyPosition());// definition of translation of model (Model/Object Coordinates --> World Coordinates)
*/

        // Apply view transform using the PMV-Tool
        // Camera positioning is steered by the interaction handler
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluLookAt(0f, 0f, interactionHandler.getEyeZ(),
                            0f, 0f, 0f,
                            0f, 1.0f, 0f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        // Switch to this vertex buffer array for drawing.
        gl.glBindVertexArray(vaoName[0]);
        // Activating the compiled shader program.
        // Could be placed into the init-method for this simple example.
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        // Transfer the PVM-Matrix (model-view and projection matrix) to the GPU
        // via uniforms
        // Transfer projection matrix via uniform layout position 0
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        // Transfer model-view matrix via layout position 1
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // Use the first 3 vertices in the VBO to draw a triangle.
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);
    }


    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x x-coordinate of the viewport
     * @param y y-coordinate of the viewport
     * @param width width of the viewport
     * @param height height of the viewport
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // Set the viewport to the entire window
        gl.glViewport(0, 0, width, height);
        // Switch the pmv-tool to perspective projection
        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        // Reset projection matrix to identity
        pmvMatrix.glLoadIdentity();
        // Calculate projection matrix
        //      Parameters:
        //          fovy (field of view), aspect ratio,
        //          zNear (near clipping plane), zFar (far clipping plane)
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.1f, 10000f);
        // Switch to model-view transform
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when OpenGL canvas ist destroyed.
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("Deleting allocated objects, incl. the shader program.");

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDeleteVertexArrays(1, vaoName,0);
        gl.glDeleteBuffers(1, vboName, 0);

        System.exit(0);
    }
}
