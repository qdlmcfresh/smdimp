/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.smd;

import com.jme.math.Vector3f;

/**
 * Class that represents SMD hbox. A hit box around a bone for collisions
 * From valve developer community : 
 * @see : <b>http://developer.valvesoftware.com/wiki/$hbox $hbox</b>
 *          (group number) (bone name) (min x) (min y) (min z) (max x) (max y) (max z)
 * @author serser
 */
public class SmdHbox {

    private int groupNumber;
    private String boneName;
    private Vector3f minPoint;
    private Vector3f maxPoint;

    public SmdHbox(int groupNumber, String boneName, Vector3f minPoint,
            Vector3f maxPoint) {
        this.groupNumber = groupNumber;
        this.boneName = boneName;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public String getBoneName() {
        return boneName;
    }

    public Vector3f getMinPoint() {
        return minPoint;
    }

    public Vector3f getMaxPoint() {
        return maxPoint;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public void setMinPoint(Vector3f minPoint) {
        this.minPoint = minPoint;
    }

    public void setMaxPoint(Vector3f maxPoint) {
        this.maxPoint = maxPoint;
    }


}
