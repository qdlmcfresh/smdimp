/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parse;

import com.base.model.smd.SmdSkeleton;
import com.base.model.smd.SmdTriangle;
import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdAnimation;
import com.base.model.smd.SmdBone;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author serser
 */
public class ParseSMDAnimation {

    private ArrayList<String> lines;
    private static String VERSION_TAG = "version";
    private static String BONE_TAG = "nodes";
    private static String SKELETON_TAG = "skeleton";
    private static String TIME_TAG = "time";
    private static String END_TAG = "end";
    private static String SPLITCHAR = " ";
    private boolean bbones = false;
    private boolean bskeleton = false;
    private ArrayList<SmdBone> bones;
    private HashMap<Integer, SmdBone> bonesMap;
    private ArrayList<SmdSkeleton> skeletonsPositions;
    private ArrayList<SmdBonePosition> bonePositions;
    SmdTriangle triangle;

    public ParseSMDAnimation(String path) {
        try {
            lines = readFile(new File(path).toURI().toURL());
            bonesMap = new HashMap<Integer, SmdBone>();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    public SmdAnimation parse() {
        SmdAnimation animation = null;
        try {
            String line;
            animation = new SmdAnimation();
            bones = new ArrayList<SmdBone>();
            bonePositions = new ArrayList<SmdBonePosition>();
            skeletonsPositions = new ArrayList<SmdSkeleton>();
            SmdSkeleton skeletonPosition = null;
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line != null && !line.equals("")) {
                    if (line.startsWith(END_TAG)) {
                        if (bbones) {
                            bbones = false;
                        } else if (bskeleton) {
                            skeletonPosition.setBones(bones.toArray(new SmdBone[]{}));
                            skeletonPosition.setPositions(bonePositions.toArray(new SmdBonePosition[]{}));
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
            Ex.printStackTrace();
        }
        return animation;
    }

    private String getVersion(String line) {
        String version = "";
        String[] lparts;
        try {
            lparts = line.split(SPLITCHAR);
            if (lparts.length > 0) {
                version = lparts[1];
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return version;
    }

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
            Ex.printStackTrace();
        }
    }

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

    private SmdSkeleton buildSkeleton() {
        SmdSkeleton smdskeleton = null;
        SmdBone[] smdBones = null;
        SmdBonePosition[] smdBonePositions = null;
        int bonesSize;
        int bonePositionsSize;
        try {
            bonesSize = this.bones.size();
            bonePositionsSize = this.bonePositions.size();
            if (bonesSize > 0 && bonePositionsSize > 0) {
                smdBones = new SmdBone[bonesSize];
                for (int i = 0; i < bonesSize; i++) {
                    smdBones[i] = this.bones.get(i);
                }
                smdBonePositions = new SmdBonePosition[bonePositionsSize];
                for (int i = 0; i < bonePositionsSize; i++) {
                    smdBonePositions[i] = this.bonePositions.get(i);
                }
                smdskeleton = new SmdSkeleton();
                smdskeleton.setBones(smdBones);
                smdskeleton.setPositions(smdBonePositions);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return smdskeleton;
    }

    private String[] clean(String[] original, int positions) {
        String[] cleaned = null;
        try {
            cleaned = new String[positions];
            int j = 0;
            boolean sequence = false;
            for (int i = 0; i < original.length; i++) {
                if (!original[i].equals("")) {
                    if (original[i].startsWith("\"") && !original[i].endsWith("\"")) {
                        cleaned[j] = original[i];
                        sequence = true;
                    } else if (!original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned[j] = cleaned[j].concat(original[i]);
                        sequence = false;
                        j++;
                    } else if (original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned[j] = original[i];
                        j++;
                    } else {
                        if (sequence) {
                            cleaned[j] = cleaned[j].concat(original[i]);
                        } else {
                            cleaned[j] = original[i];
                            j++;
                        }
                    }
                }
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }

        return cleaned;
    }

    public static ArrayList<String> readFile(URL file) {
        ArrayList<String> lines = null;
        BufferedReader reader;
        try {
            lines = new ArrayList<String>();
            reader = new BufferedReader(new InputStreamReader(file.openStream()));
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                if (line != null && !line.equals("")) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            reader = null;
        }
        return lines;
    }
}
