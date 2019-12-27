package VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP;

/**
 * Java class for creating vertex and buffer data for drawing a sphere
 * using Jogl/OpenGL.
 * Intended to be used for an OpenGL scene renderer.
 * The resolution of the sphere is held as instance variables.
 * @author Karsten Lehn
 * @version 21.10.2017, 27.10.2017
 */
public class Sphere {
    private int verticalResolution;
    private int horizontalResolution;
    private int noOfIndices;

    public Sphere(int verticalResolution, int horizontalResolution) {
        this.verticalResolution = verticalResolution;
        this.horizontalResolution = horizontalResolution;
        noOfIndices = noOfIndicesForSphere();
    }

    /**
     * Creates vertices for a (UV)-sphere with one single color and normal vectors.
     * To be used together with makeIndicesForTriangleStrip().
     * @param radius radius of the sphere
     * @param color three dimensional color vector for each vertex
     * @return list of vertices
     */
    public float[] makeVertices(float radius, float[] color)
    {
        // Using spherical coordinates to create the vertices
        int noOfComponents = 3 + 3 + 3; // 3 position coordinates, 3 color coordinates, 3 normal coordinates
        float[] vertices = new float[(verticalResolution+1) * horizontalResolution * noOfComponents];
        int vertexNumberInc = 3 + 3 + 3; // three position coordinates, three color values, three normal coordinates
        int vertexNumber = 0;

        float elevation = 0;
        float elevationInc = (float) (Math.PI / verticalResolution);
        float azimuth = 0;
        float azimuthInc = (float) (2*Math.PI / horizontalResolution);
        for(int elevationIndex = 0; elevationIndex <= verticalResolution; elevationIndex++) {
            azimuth = 0;
            for(int azimuthIndex = 0; azimuthIndex < horizontalResolution; azimuthIndex++) {
                // position coordinates in spherical coordinates
                float xPos = radius * (float) (Math.sin(elevation) * Math.cos(azimuth));
                float yPos = radius * (float) (Math.sin(elevation) * Math.sin(azimuth));
                float zPos = radius * (float) Math.cos(elevation);
                vertices[vertexNumber] = xPos;
                vertices[vertexNumber+1] = yPos;
                vertices[vertexNumber+2] = zPos;
                // color coordinates (for all vertices the same)
                vertices[vertexNumber+3] = color[0];
                vertices[vertexNumber+4] = color[1];
                vertices[vertexNumber+5] = color[2];
                // coordinates of normal vector
                // for a sphere this vector is identical to the normalizes position vector
                float normalizationFactor = 1 / (float) Math.sqrt((xPos * xPos) + (yPos * yPos) + (zPos * zPos));
                vertices[vertexNumber+6] = xPos * normalizationFactor;
                vertices[vertexNumber+7] = yPos * normalizationFactor;
                vertices[vertexNumber+8] = zPos * normalizationFactor;

                vertexNumber += vertexNumberInc;
                azimuth += azimuthInc;
            }
            elevation += elevationInc;
        }
        return vertices;
    }

    /**
     * Creates indices for drawing a sphere with glDrawElements().
     * To be used together with makeVertices().
     * To be used with "glDrawElements" and "GL_TRIANGLE_STRIP".
     * @return indices into the vertex array of the sphere
     */
    public int[] makeIndicesForTriangleStrip() {

        // Indices to refer to the number of the sphere vertices
        // defined in makeVertices()
        int[] indices = new int[getNoOfIndices()];
        int index = 0;
        for (int vIndex = 1; vIndex <= verticalResolution; vIndex++)
            for (int hIndex = 0; hIndex < horizontalResolution; hIndex++) {
                indices[index] = ((vIndex-1) * horizontalResolution) + hIndex;
                index++;
                indices[index] = ((vIndex) * horizontalResolution) + hIndex;
                index++;
            }

        return indices;
    }

    /**
     * Computes the number of indices of a sphere for the draw call.
     * @return number of indices the index buffer
     */
    private int noOfIndicesForSphere() {
        return 2 * verticalResolution * horizontalResolution;
    }

    /**
     * Returns the number of indices of a sphere for the draw call.
     * @return number of indices the index buffer
     */
    public int getNoOfIndices() {
        return noOfIndices;
    }
}
