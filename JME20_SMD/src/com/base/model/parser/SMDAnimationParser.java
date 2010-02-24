/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parser;

import com.base.model.smd.SmdSkeleton;
import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdAnimation;
import com.base.model.smd.SmdBone;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that parses .smd file that contains a sequence. Recovers
 * position data for each bone in each animation frame.
 * @see http://developer.valvesoftware.com/wiki/SMD
 * @author serser
 */
public class SMDAnimationParser extends SMDParser {

    private static final String VERSION_TAG = "version";
    private static final String BONE_TAG = "nodes";
    private static final String SKELETON_TAG = "skeleton";
    private static final String TIME_TAG = "time";
    private static final String END_TAG = "end";
    private String filePath;
    private boolean bbones = false;
    private boolean bskeleton = false;
    private ArrayList<SmdBone> bones;
    private HashMap<Integer, SmdBone> bonesMap;
    private ArrayList<SmdSkeleton> skeletonsPositions;
    private ArrayList<SmdBonePosition> bonePositions;
    private static final Logger logger = Logger.getLogger(
            SMDAnimationParser.class.getName());

    /**
     * Instantiates new .smd animation file parser. The parser recovers the
     * skeleton positions(for each frame) and bones.
     * @param path String with path of .smd animation file.
     */
    public SMDAnimationParser(String path) {
        try {
            filePath = path;
            bonesMap = new HashMap<Integer, SmdBone>();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    /**
     * Method that recovers skeleton and skeleton position for SmdAnimation.
     * @return builded SmdAnimation.
     */
    public SmdAnimation parse() {
        SmdAnimation animation = null;
        try {
            String line;
            animation = new SmdAnimation();
            bones = new ArrayList<SmdBone>();
            bonePositions = new ArrayList<SmdBonePosition>();
            skeletonsPositions = new ArrayList<SmdSkeleton>();
            SmdSkeleton skeletonPosition = null;
            lines = readFile(new File(filePath).toURI().toURL());
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line != null && !line.equals("")) {
                    if (line.startsWith(END_TAG)) {
                        if (bbones) {
                            bbones = false;
                        } else if (bskeleton) {
                            skeletonPosition.setBones(bones.toArray(new SmdBone[]{}));
                            skeletonPosition.setPositions(bonePositions.toArray(
                                    new SmdBonePosition[]{}));
                            skeletonsPositions.add(skeletonPosition);
                            bskeleton = false;
                        } else {
                            return animation;
                        }
                    } else if (line.startsWith(BONE_TAG) || bbones) {
                        bbones = true;
                        this.getBones(line);
                    } else if (line.startsWith(TIME_TAG)) {
                        bskeleton = true;
                        if (skeletonPosition != null) {
                            skeletonPosition.setBones(bones.toArray(new SmdBone[]{}));
                            skeletonPosition.setPositions(bonePositions.toArray(new SmdBonePosition[]{}));
                            skeletonsPositions.add(skeletonPosition);
                        }
                        bonePositions = new ArrayList<SmdBonePosition>();
                        skeletonPosition = new SmdSkeleton();
                    } else if (line.startsWith(SKELETON_TAG) || bskeleton) {
                        bskeleton = true;
                        this.getBonePositions(line);
                    }
                }
            }
            animation.seSkeletonPositions(skeletonsPositions.toArray(new SmdSkeleton[]{}));
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error parsing .smd animation file " + filePath
                    + " : ", Ex);
        }
        return animation;
    }

    /**
     * Method builds a SmdBone from line, recovers bone index, bone name and
     * bone patent .
     * @param line that contains the SmdBone.
     */
    private void getBones(String line) {
        SmdBone bone;
        String[] lparts;
        try {
            if (!line.startsWith(BONE_TAG)) {
                lparts = line.split(SPLITCHAR);
                lparts = clean(lparts, 3);
                bone = new SmdBone(new Integer(lparts[0]).intValue(), lparts[1], new Integer(lparts[2]).intValue());
                bones.add(bone);
                bonesMap.put(bone.getBoneIndex(), bone);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error building bone from line : " + line
                    + " : ", Ex);
        }
    }
/**
 * Method builds a SmdBonePosition from line, recovers bone index, position,
 * rotation and traslation. Stores SmdBonePosition in a list.
 * @param line line that contains the SmdBonePosition.
 */
    private void getBonePositions(String line) {
        SmdBonePosition bonePos;
        String[] lparts;
        try {
            if (!line.startsWith(SKELETON_TAG) && !line.startsWith(TIME_TAG)) {
                lparts = line.split(SPLITCHAR);
                lparts = clean(lparts, 7);
                bonePos = new SmdBonePosition();
                bonePos.setBoneIndex(new Integer(lparts[0]).intValue());
                bonePos.setPosition(Float.parseFloat(lparts[1]), Float.parseFloat(lparts[2]), Float.parseFloat(lparts[3]));
                bonePos.setRotation(Float.parseFloat(lparts[4]), Float.parseFloat(lparts[5]), Float.parseFloat(lparts[6]));
                bonePositions.add(bonePos);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
