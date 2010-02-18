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
public class SmdBone {

    private int boneIndex;
    private String boneName;
    private int boneParent;
    private ArrayList<SmdVertex> vertexInfluence;

    public SmdBone(int boneIndex, String boneName, int boneParent) {
        this.boneIndex = boneIndex;
        this.boneName = boneName;
        this.boneParent = boneParent;
    }

    public int getBoneIndex() {
        return boneIndex;
    }

    public String getBoneName() {
        return boneName;
    }

    public int getBoneParent() {
        return boneParent;
    }

    public void setBoneIndex(int boneIndex) {
        this.boneIndex = boneIndex;
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public void setBoneParent(int boneParent) {
        this.boneParent = boneParent;
    }

    public boolean addVertex(SmdVertex vert) {
        if (this.vertexInfluence == null) {
            this.vertexInfluence = new ArrayList<SmdVertex>();
        }
        return this.vertexInfluence.add(vert);
    }

    public ArrayList<SmdVertex> getVertexInfluence() {
        return vertexInfluence;
    }

}
