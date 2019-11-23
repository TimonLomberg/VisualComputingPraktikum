package VisualComputingPraktikum.computergrafik.objloader;

import de.hshl.obj.loader.OBJLoader;
import de.hshl.obj.loader.Resource;
import de.hshl.obj.loader.objects.Mesh;

import java.io.IOException;
import java.nio.file.Paths;

public class MyObjLoader {

    OBJLoader loader;

    public MyObjLoader () {
        loader = new OBJLoader();
        loader.setLoadTextureCoordinates(true);
        loader.setLoadNormals(true);
        // loader.setGenerateIndexedMeshes(true);
    }

    public static void main(String[] args) {
        MyObjLoader obj = new MyObjLoader();
        obj.loadFile();
    }

    public float[] loadFile() {
        try {
            Resource file = Resource.bundled(Paths.get("resources/objects/triangle.obj"));
            Mesh mesh = loader.loadMesh(file);
            System.out.println(mesh);
            float[] vertices = mesh.getVertices();
            return vertices;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }



}
