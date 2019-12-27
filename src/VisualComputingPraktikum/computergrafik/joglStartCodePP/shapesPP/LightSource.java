package VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP;

/**
 *  Java class holding parameters for a light source.
 *  @author Karsten Lehn
 *  @version 23.10.2017, 25.10.2017
 */
public class LightSource {
    private float[] position;
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;

    public LightSource() {
    }

    public LightSource(float[] position, float[] ambient, float[] diffuse, float[] specular) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public void setAmbient(float[] ambient) {
        this.ambient = ambient;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float[] diffuse) {
        this.diffuse = diffuse;
    }

    public float[] getSpecular() {
        return specular;
    }

    public void setSpecular(float[] specular) {
        this.specular = specular;
    }

}
