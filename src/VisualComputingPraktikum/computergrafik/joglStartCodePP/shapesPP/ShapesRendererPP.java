package VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP; /**
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
import java.nio.IntBuffer;

import VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode.Tracking;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;

/**
 * Performs the OpenGL graphics processing using the Programmable Pipeline and the
 * OpenGL Core profile
 *
 * Starts an animation loop.
 * Zooming and rotation of the Camera is included (see InteractionHandler).
 * 	Use: left/right/up/down-keys and +/-Keys and mouse
 * Enables drawing of simple shapes: box, sphere, cone (frustum) and roof
 * Serves as a template (start code) for setting up an OpenGL/Jogl application
 * using a vertex and fragment shader.
 *
 * Please make sure setting the file path and names of the shader correctly (see below).
 *
 * Core code is based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn
 * @version 22.10.2017
 *
 */
public class ShapesRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    // taking shader source code files from relative path
    private final String shaderPath = ".\\.\\resources\\shader\\";
    // Shader for object 0
    private final String vertexShader0FileName = "O0_Basic.vert";
    private final String fragmentShader0FileName = "Ambient.frag";
    // Shader for object 1
    private final String vertexShader1FileName = "O1_Basic.vert";
    private final String fragmentShader1FileName = "O1_Basic.frag";

    // Shader for object 2
    private final String vertexShader2FileName = "O2_Basic.vert";
    private final String fragmentShader2FileName = "O2_Basic.frag";

    // Shader for object 3
    private final String vertexShader3FileName = "O3_Basic.vert";
    private final String fragmentShader3FileName = "O3_Basic.frag";

    private ShaderProgram shaderProgram0;
    private ShaderProgram shaderProgram1;
    private ShaderProgram shaderProgram2;
    private ShaderProgram shaderProgram3;

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Names of vertex array objects
    private int[] vboName;	// Names of vertex buffer objects
    private int[] iboName;	// Names of index buffer objects

    // Create objects for the scene
    // The box and roof do not need objects because all methods of these classes are static
    private Sphere sphere0;
    private Cone cone0;

    // Object for handling keyboard and mouse interaction
    private InteractionHandler interactionHandler;
    // Projection model view matrix tool
    private PMVMatrix pmvMatrix;

    float[] object0Color = {0.1f, 0.1f, 0.8f};
    public FloatBuffer color0Buffer = FloatBuffer.wrap(object0Color);
    float[] object0LightColor = {1f, 1f, 1f};
    public FloatBuffer lightColor0Buffer = FloatBuffer.wrap(object0LightColor);

    /**
     * Standard constructor for object creation.
     */
    public ShapesRendererPP() {
        // Create the canvas with default capabilities
        super();
        // Add this object as OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Create the canvas with the requested OpenGL capabilities
     * @param capabilities The capabilities of the canvas, including the OpenGL profile
     */
    public ShapesRendererPP(GLCapabilities capabilities) {
        // Create the canvas with the requested OpenGL capabilities
        super(capabilities);
        // Add this object as an OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Helper method for creating an interaction handler object and registering it
     * for key press and mouse interaction callbacks.
     */
    private void createAndRegisterInteractionHandler() {
        // The constructor call of the interaction handler generates meaningful default values
        // Nevertheless the start parameters can be set via setters
        // (see class definition of the interaction handler)
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
        GL3 gl = drawable.getGL().getGL3();

        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Verify if VBO-Support is available
        if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 4;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoName = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        // Initialize objects to be drawn (see respective sub-methods)
        initObject0(gl);
        initObject1(gl);
        //initObject2(gl);
        //initObject3(gl);
        // END: Preparing scene

        // Switch on back face culling
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
//        gl.glCullFace(GL.GL_FRONT);
        // Switch on depth test
        gl.glEnable(GL.GL_DEPTH_TEST);

        // defining polygon drawing mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_FILL);
//        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_LINE);

        // Create projection-model-view matrix
        pmvMatrix = new PMVMatrix();

        // Start parameter settings for the interaction handler might be called here
        interactionHandler.setEyeZ(5.5f);
        // END: Preparing scene
    }

    /**
     * Initializes the GPU for drawing object0
     * @param gl OpenGL context
     */
    private void initObject0(GL3 gl) {
        // BEGIN: Prepare a sphere for drawing (object 0)
        // create sphere data for rendering a sphere using an index array into a vertex array
        gl.glBindVertexArray(vaoName[0]);
        // Shader program for object 0
        shaderProgram0 = new ShaderProgram(gl);
        shaderProgram0.loadShaderAndCreateProgram(shaderPath,
                vertexShader0FileName, fragmentShader0FileName);

        float[] color0 = {0.8f, 0.1f, 0.1f};
        int paraLoc = gl.glGetUniformLocation(shaderProgram0.getShaderProgramID(), "objectColor");

        sphere0 = new Sphere(64, 64);
        float[] sphereVertices = sphere0.makeVertices(0.5f, color0);
        int[] sphereIndices = sphere0.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, sphereVertices.length * 4,
                FloatBuffer.wrap(sphereVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[0]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sphereIndices.length * 4,
                IntBuffer.wrap(sphereIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // Defining input variables for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare sphere for drawing
    }

    /**
     * Initializes the GPU for drawing object1
     * @param gl OpenGL context
     */
    private void initObject1(GL3 gl) {
        // BEGIN: Prepare cube for drawing (object 1)
        gl.glBindVertexArray(vaoName[1]);
        shaderProgram1 = new ShaderProgram(gl);
        shaderProgram1.loadShaderAndCreateProgram(shaderPath,
                vertexShader1FileName, fragmentShader1FileName);

        float[] color1 = {0.1f, 0.1f, 0.8f};
        float[] cubeVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        int[] cubeIndices = Box.makeBoxIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[1]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[1]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare cube for drawing
    }

    /**
     * Initializes the GPU for drawing object2
     * @param gl OpenGL context
     */
    private void initObject2(GL3 gl) {
        // BEGIN: Prepare cone (frustum) for drawing (object 2)
        // create cone (frustum) data for rendering a cone (frustum) using an index array into a vertex array
        gl.glBindVertexArray(vaoName[2]);
        shaderProgram2 = new ShaderProgram(gl);
        shaderProgram2.loadShaderAndCreateProgram(shaderPath,
                vertexShader2FileName, fragmentShader2FileName);

        float[] color2 = {0.2f, 0.8f, 0.2f};
        cone0 = new Cone(64);
        float[] coneVertices = cone0.makeVertices(0.2f, 0.6f, 1f, color2);
        int[] coneIndices = cone0.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[2]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[2]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

        // Activate and arrange vertex buffer object data for the vertex shader
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare cone (frustum) for drawing
    }

    /**
     * Initializes the GPU for drawing object3
     * @param gl OpenGL context
     */
    private void initObject3(GL3 gl) {
        // BEGIN: Prepare roof for drawing (object 3)
        // create data for rendering a roof using an index array into a vertex array
        gl.glBindVertexArray(vaoName[3]);
        shaderProgram3 = new ShaderProgram(gl);
        shaderProgram3.loadShaderAndCreateProgram(shaderPath,
                vertexShader3FileName, fragmentShader3FileName);

        float[] color3 = {0.8f, 0.8f, 0.1f};
        float[] roofVertices = Roof.makeVertices(0.8f, 1.1f, 0.5f, color3);
        int[] roofIndices = Roof.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[3]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, roofVertices.length * 4,
                FloatBuffer.wrap(roofVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[3]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, roofIndices.length * 4,
                IntBuffer.wrap(roofIndices), GL.GL_STATIC_DRAW);

        // Activate and arrange vertex buffer object data for the vertex shader
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare roof for drawing
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called by the OpenGL animator for every frame.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Background color of the canvas
        gl.glClearColor(0.97f, 0.97f, 0.97f, 1.0f);

        // For monitoring the interaction settings
/*        System.out.println("Camera: z = " + interactionHandler.getEyeZ() + ", " +
                "x-Rot: " + interactionHandler.getAngleXaxis() +
                ", y-Rot: " + interactionHandler.getAngleYaxis() +
                ", x-Translation: " + interactionHandler.getxPosition()+
                ", y-Translation: " + interactionHandler.getyPosition());// definition of translation of model (Model/Object Coordinates --> World Coordinates)
*/
        // Using the PMV-Tool for geometric transforms
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();
        // Setting the camera position, based on user input
        pmvMatrix.gluLookAt(0f, 0f, interactionHandler.getEyeZ(),
                            0f, 0f, 0f,
                            0f, 1.0f, 0f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        // Transform for the complete scene
//        pmvMatrix.glTranslatef(1f, 0.2f, 0f);

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef((float)Tracking.green.get(0), (float)Tracking.green.get(1), (float)Tracking.green.get(2));
        //displayObject0(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef((float) Tracking.red.get(0), (float) Tracking.red.get(1), (float) Tracking.red.get(2));
        pmvMatrix.glRotatef(45f, 0f, 1f, 0f);
        displayObject1(gl);
        pmvMatrix.glPopMatrix();

        /*pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f, -1f, 0f);
        displayObject2(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(1.5f, 0f, 0f);
        displayObject3(gl);
        pmvMatrix.glPopMatrix();*/
    }

    private void displayObject0(GL3 gl) {
        shaderProgram0.use();
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniform3fv(shaderProgram0.getShaderProgramID(), 1 , color0Buffer);
        gl.glUniform3fv(shaderProgram0.getShaderProgramID(), 1 , lightColor0Buffer);
        gl.glBindVertexArray(vaoName[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, sphere0.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObject1(GL3 gl) {
       shaderProgram1.use();
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoName[1]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Box.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObject2(GL3 gl) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoName[2]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone0.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObject3(GL3 gl) {
        gl.glUseProgram(shaderProgram3.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoName[3]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Roof.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when OpenGL canvas ist destroyed.
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Deleting allocated objects, incl. shader program.");
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram0.deleteShaderProgram();
        shaderProgram1.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

        System.exit(0);
    }
}
