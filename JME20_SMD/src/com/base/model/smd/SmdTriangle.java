/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.smd;

import java.util.ArrayList;

/**
 *
 * @author serser
 */
public class SmdTriangle {

    private int index;
    private ArrayList<SmdVertex> verts;
    private String textureName;
    private String texturePath;

    public SmdTriangle() {
        this.verts = new ArrayList<SmdVertex>();
    }

    public void addSmdVertex(SmdVertex vertex) {
        this.verts.add(vertex);
    }

    public int getVertsSize() {
        return this.verts.size();
    }

    public SmdVertex getSmdVertex(int index) {
        return verts.get(index);
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<SmdVertex> getVerts() {
        return verts;
    }

    public void setGeomIndex(int index) {
        try {
            for (int i = 0; i < this.getVertsSize(); i++) {
                this.getSmdVertex(i).setGeomIndex(index);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
