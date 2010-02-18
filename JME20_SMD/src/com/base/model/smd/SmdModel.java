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
public class SmdModel {

    private String version;
    private String name;
    private String type;
    private SmdSkeleton skeleton;
    private ArrayList<SmdMesh> meshes;
    private ArrayList<SmdAnimation> animations;
    private ArrayList<SmdHbox> hitBoxes;

    public SmdModel(String type) {
        this.type = type;
        animations = new ArrayList<SmdAnimation>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SmdSkeleton getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(SmdSkeleton skeleton) {
        this.skeleton = skeleton;
    }

    public ArrayList<SmdMesh> getMeshes() {
        return meshes;
    }

    public ArrayList<SmdAnimation> getAnimations() {
        return animations;
    }


    public void setMeshes(ArrayList<SmdMesh> meshes) {
        this.meshes = meshes;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAnimations(ArrayList<SmdAnimation> animations) {
        this.animations = animations;
    }

    public void addSmdAnimation(SmdAnimation animation) {
        this.animations.add(animation);
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<SmdHbox> getHitBoxes() {
        return hitBoxes;
    }

    public void setHitBoxes(ArrayList<SmdHbox> hitBoxes) {
        this.hitBoxes = hitBoxes;
    }

}
