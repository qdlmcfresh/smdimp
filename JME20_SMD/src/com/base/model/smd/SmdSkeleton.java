/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.smd;

import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdBone;

/**
 *
 * @author serser
 */
public class SmdSkeleton {

    private SmdBone[] bones;
    private SmdBonePosition[] positions;

    public void setBones(SmdBone[] bones) {
        this.bones = bones;
    }

    public void setPositions(SmdBonePosition[] positions) {
        this.positions = positions;
    }

    public SmdBone[] getBones() {
        return bones;
    }

    public SmdBonePosition[] getPositions() {
        return positions;
    }

}
