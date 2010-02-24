/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.smd;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that represents smd animation ($sequence), defines a skeletal animation.
 * stores skeleton positions (bone positions in a frame).
 * @see http://developer.valvesoftware.com/wiki/$sequence
 * @author serser
 */
public class SmdAnimation {

    private String name;
    private float fps;
    private boolean blendAnimation;
    private boolean subBlendAnimation = false;
    private SmdSkeleton[] skeletonPositions;
    private ArrayList<SmdAnimation> animations;
    private static final Logger logger = Logger.getLogger(
            SmdAnimation.class.getName());

    public SmdAnimation() {
    }

    /**
     * Instantiates new Smd animation with a name and boolean 
     * @param name
     * @param blendAnimation
     */
    public SmdAnimation(String name, boolean blendAnimation) {
        this.name = name;
        this.blendAnimation = blendAnimation;
        logger.log(Level.INFO, "adding smdAnimation : " + name);
        if (blendAnimation) {
            logger.log(Level.INFO, name + " is blend animation");
            animations = new ArrayList<SmdAnimation>();
        }
    }

    /**
     * Method that recovers animation name.
     * @return String with the animation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns the animation fps
     * @return float with animation fps.
     */
    public float getFps() {
        return fps;
    }

    /**
     * Method that recovers the SkeletonPositions, each array position represents
     * a frame and Skeleton contains the bone poses.
     * @return Skeleton array with bone positions.
     */
    public SmdSkeleton[] getSkeletonPositions() {
        return skeletonPositions;
    }

    /**
     * Method that stablish the animation name
     * @param name : String with the animation name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method that stablish the animation fps.
     * @param fps float with animation fps.
     */
    public void setFps(float fps) {
        this.fps = fps;
        if (isBlendAnimation()) {
            for (int i = 0; i < this.animations.size(); i++) {
                this.animations.get(i).setFps(fps);
                this.animations.get(i).setSubBlendAnimation(true);
            }
        }
    }

    /**
     * Method that stablish SkeletonPositions, contains bone poses for each animation
     * frame.
     * @param skeletonPositions : array with SmdSkeleton.
     */
    public void seSkeletonPositions(SmdSkeleton[] skeletonPositions) {
        this.skeletonPositions = skeletonPositions;
    }

    /**
     * Method that returns if the animation is a blend animation.
     * @return boolean : true for blend animation false if not.
     */
    public boolean isBlendAnimation() {
        return blendAnimation;
    }

    /**
     * Method that stablish if the animation is blend or not
     * @param blendAnimation : boolean, true for blend animation false if not.
     */
    public void setBlendAnimation(boolean blendAnimation) {
        this.blendAnimation = blendAnimation;
    }

    /**
     * Method that adds SmdAnimation (blend animation), to the animation list
     * @param animation SmdAnimation to add.
     */
    public void addSmdAnimation(SmdAnimation animation) {
        if (animations == null) {
            animations = new ArrayList<SmdAnimation>();
        }
        this.animations.add(animation);
    }

    /**
     * Method that recovers a SmdAnimation from the list
     * @param index : int with the index.
     * @return SmdAnimation.
     */
    public SmdAnimation getSmdAnimation(int index) {
        SmdAnimation banimation = null;
        if (animations != null) {
            banimation = animations.get(index);
        }
        return banimation;
    }

    /**
     * Method that recovers the animation list size.
     * @return int with the animation list size.
     */
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
