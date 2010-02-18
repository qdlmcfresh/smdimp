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
public class SmdAnimation {

    private String name;
    private float fps;
    private boolean blendAnimation;
    private boolean subBlendAnimation = false;
    private SmdSkeleton[] skeletonPositions;
    private ArrayList<SmdAnimation> animations;

    public SmdAnimation() {
    }

    public SmdAnimation(boolean blendAnimation) {
        this.blendAnimation = blendAnimation;
        if (blendAnimation) {
            animations = new ArrayList<SmdAnimation>();
        }
    }

    public String getName() {
        return name;
    }

    public float getFps() {
        return fps;
    }

    public SmdSkeleton[] getSkeletonPositions() {
        return skeletonPositions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFps(float fps) {
        this.fps = fps;
        if (isBlendAnimation()) {
            for (int i = 0; i < this.animations.size(); i++) {
                this.animations.get(i).setFps(fps);
                this.animations.get(i).setSubBlendAnimation(true);
            }
        }
    }

    public void seSkeletonPositions(SmdSkeleton[] skeletonPositions) {
        this.skeletonPositions = skeletonPositions;
    }

    public boolean isBlendAnimation() {
        return blendAnimation;
    }

    public void setBlendAnimation(boolean blendAnimation) {
        this.blendAnimation = blendAnimation;
    }

    public void addSmdAnimation(SmdAnimation animation) {
        if (animations == null) {
            animations = new ArrayList<SmdAnimation>();
        }
        this.animations.add(animation);
    }

    public SmdAnimation getSmdAnimation(int index) {
        SmdAnimation banimation = null;
        if (animations != null) {
            banimation = animations.get(index);
        }
        return banimation;
    }

    public int getSmdAnimationSize() {
        int animationsSize = 0;
        if (animations != null) {
            animationsSize = animations.size();
        }
        return animationsSize;
    }

    public boolean isSubBlendAnimation() {
        return subBlendAnimation;
    }

    public void setSubBlendAnimation(boolean subBlendAnimation) {
        this.subBlendAnimation = subBlendAnimation;
    }

}
