package VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP;

/**
 * Java class for creating vertex and buffer data for drawing a
 * roof like shape, i.e. a cylinder with a triangular base surface
 * using Jogl/OpenGL.
 * Intended to be used for an OpenGL scene renderer.
 * All methods are static.
 *
 * @author Karsten Lehn
 * @version 22.10.2017, 27.10.2017
 */
public class Roof {

    /**
     * Creates vertices for a roof like shape with one single color and
     * normal vectors.
     * To be used together with makeIndicesForTriangleStrip().
     * The centre of gravity of the shape lies in the origin.
     * The base side of the top and bottom triangles is oriented
     * parallel to the z-axis (at z-coordinate: (-width/3)).
     * The width of the shape is the length of the geometric height
     * of the base triangles over their base side. The top of the
     * base triangle is located at the x-coordinate ((2/3) * width)
     * and the z-coordinate 0.
     *
     * @param width distance between base side and top (x-direction) of the base triangle
     * @param height distance between bottom and top triangular surfaces
     *               (length of the shape in y-direction)
     * @param depth length of the base side (z-direction) of the base triangles
     * @param color three dimensional color vector for each vertex
     */
    public static float[] makeVertices(float width, float height, float depth, float[] color) {

        // BEGIN: Defining the x- and z-coordinates of the vertices
        // Front vertex of base side of the base triangle
        float xFrontVertex = - width/3;
        float zFrontVertex = depth/2;
        // Vertex defining the top/peak (over the base side) of the triangle
        // lies at the right side in the xz-plane over the base side of the triangle
        float xSideVertex = (2/3) * width;
        float zSideVertex = 0;
        // Back vertex of base side of the base triangle
        float xBackVertex = - width/3;
        float zBackVertex = -depth/2;
        // END: Defining the x- and z-coordinates of the vertices

        // BEGIN: Defining the y-coordinates of the vertices
        float yTopTriangle = height/2;
        float yBottomTriangle = -height/2;
        // END: Defining the y-coordinates of the vertices

        // BEGIN: Defining the x- and z-coordinates of the normal vectors
        // for the side surfaces.
        // Angle of triangle at front vertex:
        float alpha = (float) Math.atan(2 * width/depth);
        // Normal0: normal vector for side created by the front and side vertex
        float xNormal0 = (float) Math.sin(alpha);
        float zNormal0 = (float) Math.cos(alpha);
        // Normal1: normal vector for side created by the back and side vertex
        // coordinates of normal 0  mirrored at the x-axis
        float xNormal1 = xNormal0;
        float zNormal1 = -zNormal0;
        // END: Defining the x- and z-coordinates of the normal vectors

        // Defining normalized normal vectors for side surfaces
        float normalizationFactor = 1f / ((float) Math.sqrt((xNormal0 * xNormal0) + (zNormal0 * zNormal0)));
        float[] normal0 = {xNormal0 * normalizationFactor, 0, zNormal0 * normalizationFactor};
        normalizationFactor = 1f / ((float) Math.sqrt((xNormal1 * xNormal1) + (zNormal1 * zNormal1)));
        float[] normal1 = {xNormal1 * normalizationFactor, 0, zNormal1 * normalizationFactor};
        float[] normal2 = {-1, 0, 0};

        // normal vector for top surface
        float[] topNormal = {0, 1, 0};
        // normal vector for buttom surface
        float[] bottomNormal = {0, -1, 0};


        // vertices for the top and bottom triangles are duplicated
        // for correct normal vector orientation
        int noOfComponents = 3 + 3 + 3; // 3 position coordinates, 3 color coordinates, 3 normal coordinates
        // 3 + // vertices for the top triangle
        // 6 + // vertices for the top edge of the surface
        // 2 vertices for each of the 3 sides
        // 6 + // vertices for the bottom edge of the surface
        // 2 vertices for each of the 3 sides
        // 3 // vertices for the bottom triangle
        // = 18 vertices in total
        int noOfVertices = 18;

        // Vertex definition
        // Vertices for the top and bottom triangles are duplicated
        // for correct normal vector orientation.
        // Interleaved: 3 position coordinates, 3 color coordinates, 3 normal coordinates
        float[] vertices = {
                // BEGIN: vertices for surface of top triangle
                // index 0
                xFrontVertex, yTopTriangle, zFrontVertex,   // front vertex position
                color[0], color[1], color[2],
                topNormal[0], topNormal[1], topNormal[2],   // top normal
                // index 1
                xSideVertex, yTopTriangle, zSideVertex,     // side vertex position
                color[0], color[1], color[2],
                topNormal[0], topNormal[1], topNormal[2],   // top normal
                // index 2
                xBackVertex, yTopTriangle, zBackVertex,     // back vertex position
                color[0], color[1], color[2],
                topNormal[0], topNormal[1], topNormal[2],   // top normal
                // END: vertices for surface of top triangle

                // BEGIN: vertices for top edge of side surface
                // index 3
                xFrontVertex, yTopTriangle, zFrontVertex,   // front vertex position
                color[0], color[1], color[2],
                normal0[0], normal0[1], normal0[2],         // Normal 0
                // index 4
                xSideVertex, yTopTriangle, zSideVertex,     // side vertex position
                color[0], color[1], color[2],
                normal0[0], normal0[1], normal0[2],         // Normal 0
                // index 5
                xSideVertex, yTopTriangle, zSideVertex,     // side vertex position
                color[0], color[1], color[2],
                normal1[0], normal1[1], normal1[2],         // Normal 1
                // index 6
                xBackVertex, yTopTriangle, zBackVertex,     // back vertex position
                color[0], color[1], color[2],
                normal1[0], normal1[1], normal1[2],         // Normal 1
                // index 7
                xBackVertex, yTopTriangle, zBackVertex,     // back vertex position
                color[0], color[1], color[2],
                normal2[0], normal2[1], normal2[2],         // Normal 2
                // index 8
                xFrontVertex, yTopTriangle, zFrontVertex,   // front vertex position
                color[0], color[1], color[2],
                normal2[0], normal2[1], normal2[2],         // Normal 2
                // END: vertices for top edge of side surface

                // BEGIN: vertices for bottom edge of side surface
                // index 9
                xFrontVertex, yBottomTriangle, zFrontVertex,    // front vertex position
                color[0], color[1], color[2],
                normal0[0], normal0[1], normal0[2],             // Normal 0
                // index 10
                xSideVertex, yBottomTriangle, zSideVertex,      // side vertex position
                color[0], color[1], color[2],
                normal0[0], normal0[1], normal0[2],             // Normal 0
                // index 11
                xSideVertex, yBottomTriangle, zSideVertex,      // side vertex position
                color[0], color[1], color[2],
                normal1[0], normal1[1], normal1[2],             // Normal 1
                // index 12
                xBackVertex, yBottomTriangle, zBackVertex,      // back vertex position
                color[0], color[1], color[2],
                normal1[0], normal1[1], normal1[2],             // Normal 1
                // index 13
                xBackVertex, yBottomTriangle, zBackVertex,      // back vertex position
                color[0], color[1], color[2],
                normal2[0], normal2[1], normal2[2],             // Normal 2
                // index 14
                xFrontVertex, yBottomTriangle, zFrontVertex,    // front vertex position
                color[0], color[1], color[2],
                normal2[0], normal2[1], normal2[2],             // Normal 2
                // END: vertices for bottom edge of side surface

                // BEGIN: vertices for surface of bottom triangle
                // index 15
                xFrontVertex, yBottomTriangle, zFrontVertex,        // front vertex position
                color[0], color[1], color[2],
                bottomNormal[0], bottomNormal[1], bottomNormal[2],  // bottom normal
                // index 16
                xSideVertex, yBottomTriangle, zSideVertex,          // side vertex position
                color[0], color[1], color[2],
                bottomNormal[0], bottomNormal[1], bottomNormal[2],  // bottom normal
                // index 17
                xBackVertex, yBottomTriangle, zBackVertex,          // back vertex position
                color[0], color[1], color[2],
                bottomNormal[0], bottomNormal[1], bottomNormal[2],  // bottom normal
                // END: vertices for surface of top triangle
        };
        return vertices;
    }

    /**
     * Creates indices for drawing the shape with glDrawElements().
     * To be used together with makeVertices().
     * To be used with "glDrawElements" and "GL_TRIANGLE_STRIP".
     * @return indices into the vertex array of the shape
     */
    public static int[] makeIndicesForTriangleStrip() {
        // Indices to reference the number of the shape vertices
        // defined in makeVertices()
        int[] indices = {
                // Note: back faces are drawn,
                // but drawing is faster than using "GL_TRIANGLES"
                0, 1, 2, 0,     // top triangular surface
                3, 9, 4, 10,    // surface with normal 0 (between front and side vertex)
                5, 11, 6, 12,   // surface with normal 1 (between side and back vertex)
                7, 13, 8, 14,   // surface with normal 2 (between back and front vertex)
                15, 15, 16, 17      // bottom triangular surface
        };
        return indices;
    }

    /**
     * Return the number of indices for drawing the shape.
     * @return number of indices
     */
    public static int getNoOfIndices() {
        return 20;
    }
}
