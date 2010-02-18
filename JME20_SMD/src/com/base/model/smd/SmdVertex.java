/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.smd;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * Clase que representa un vertice.
 * @author serser
 */
public class SmdVertex {

    private Vector3f position;
    private Vector3f normals;
    private Vector2f textCoord;
    private int boneIndex;
    private int vertIndex;
    private int geomIndex;
    private float weight = 1f;

    /**
     * Method that stablish de position of the vertex.
     * @param x float with x position
     * @param y float with y position
     * @param z float with z position
     */
    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    /**
     * Method that returns de position vector.
     * @return Vector3f with the vector position.
     */
    public Vector3f getPosition() {
        return position;
    }

    public void setNormals(float x, float y, float z) {
        this.normals = new Vector3f(x, y, z);
    }

    public Vector3f getNormals() {
        return normals;
    }

    public void setTextCoord(float x, float y) {
        this.textCoord = new Vector2f(x, y);
    }

    public Vector2f getTextCoord() {
        return textCoord;
    }

    public void setBoneIndex(int boneIndex) {
        this.boneIndex = boneIndex;
    }

    public int getBoneIndex() {
        return boneIndex;
    }

    public int getVertIndex() {
        return vertIndex;
    }

    public void setVertIndex(int vertIndex) {
        this.vertIndex = vertIndex;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getGeomIndex() {
        return geomIndex;
    }

    public void setGeomIndex(int geomIndex) {
        this.geomIndex = geomIndex;
    }
    
}
