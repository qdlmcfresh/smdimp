/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parse;

import com.base.model.smd.SmdSkeleton;
import com.base.model.smd.SmdVertex;
import com.base.model.smd.SmdTriangle;
import com.base.model.smd.SmdMesh;
import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdBone;
import com.base.model.smd.SmdModel;
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
public class ParseSMDModel {

    private ArrayList<String> lines;
    private static String VERSION_TAG = "version";
    private static String BONE_TAG = "nodes";
    private static String SKELETON_TAG = "skeleton";
    private static String TRIANGLES_TAG = "triangles";
    private static String TIME_TAG = "time";
    private static String END_TAG = "end";
    private static String SPLITCHAR = " ";
    private boolean bbones = false;
    private boolean bskeleton = false;
    private boolean btriangles = false;
    private ArrayList<SmdBone> bones;
    private HashMap<Integer, SmdBone> bonesMap;
    private ArrayList<SmdBonePosition> bonePositions;
    private ArrayList<SmdMesh> meshes;
    private ArrayList<SmdTriangle> triangles;
    private ArrayList<SmdVertex> vertex;
    private String name;
    private int vertIndex = 0;
    SmdTriangle triangle;
    private String textureName = "";
    private int geomIndex = 0;
    private String type;

    public ParseSMDModel(String path, String type) {
        try {
            this.type = type;
            lines = readFile(new File(path).toURI().toURL());
            bonesMap = new HashMap<Integer, SmdBone>();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    public SmdModel parse() {
        SmdModel model = null;
        try {
            String line;
            model = new SmdModel(type);
            bones = new ArrayList<SmdBone>();
            bonePositions = new ArrayList<SmdBonePosition>();
            meshes = new ArrayList<SmdMesh>();
            triangles = new ArrayList<SmdTriangle>();
            vertex = new ArrayList<SmdVertex>();
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line != null && !line.equals("")) {
                    if (line.startsWith(END_TAG)) {
                        if (bbones) {
                            bbones = false;
                        } else if (bskeleton) {
                            bskeleton = false;
                        } else if (btriangles) {
                            if (triangle != null) {//last triangle.
                                this.triangles.add(triangle);
                            }
                            btriangles = false;
                        } else {
                            return model;
                        }
                    } else if (line.startsWith(VERSION_TAG)) {
                        model.setVersion(getVersion(line));
                    } else if (line.startsWith(BONE_TAG) || bbones) {
                        bbones = true;
                        this.getBones(line);
                    } else if (line.startsWith(SKELETON_TAG) || bskeleton) {
                        bskeleton = true;
                        this.getBonePositions(line);
                    } else if (line.startsWith(TRIANGLES_TAG) || btriangles) {
                        btriangles = true;
                        this.getMeshes(line);
                    }
                }
            }
            model.setSkeleton(buildSkeleton());
            model.setMeshes(buildMeshes());
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return model;
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

    /**
     * Method that creates submeshes
     * @param line : String text line from smd file.
     */
    private void getMeshes(String line) {
        String[] lparts;
        if (!line.startsWith(TRIANGLES_TAG)) {
            lparts = line.split(SPLITCHAR);
            if (lparts.length == 1) {//checks texture name
                //creates new submesh with a new geomIndex, and initialize the vert index.
                if (!this.textureName.equals(line) && !this.textureName.equals("")) {
                    this.geomIndex++;
                    vertIndex = 0;
                }
                textureName = line;
            }
            getTriangles(lparts, this.geomIndex);
        }
    }

    /**
     * Method that recovers the triangles.
     * @param line : String with text line to analize.
     * @param geomIndex
     */
    private void getTriangles(String [] splitLine, int geomIndex) {
        SmdVertex vertx;
        try {
            if (splitLine.length == 1) {
                if (triangle != null) {
                    triangles.add(triangle);
                }
                triangle = new SmdTriangle();
                triangle.setTextureName(splitLine[0]);
            } else {//recovers and creates SmdVertex.
                splitLine = clean(splitLine, 9);
                vertx = new SmdVertex();
                vertx.setBoneIndex(new Integer(splitLine[0]).intValue());
                vertx.setPosition(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
                vertx.setNormals(Float.parseFloat(splitLine[4]), Float.parseFloat(splitLine[5]), Float.parseFloat(splitLine[6]));
                vertx.setTextCoord(Float.parseFloat(splitLine[7]), Float.parseFloat(splitLine[8]));
                vertx.setVertIndex(vertIndex);
                vertx.setGeomIndex(geomIndex);
                buildBoneInfluence(vertx);
                triangle.addSmdVertex(vertx);
                vertIndex++;
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    private ArrayList<SmdMesh> buildMeshes() {
        SmdTriangle[] smdtriangles = null;
        String txName = "";
        ArrayList<SmdTriangle> striangles = null;
        SmdMesh smesh;
        try {
            for (int i = 0; i < triangles.size(); i++) {
                //Stablish geom index for triangle
                //COMMENT THIS
                //triangles.get(i).setGeomIndex(this.meshes.size());
                if (!triangles.get(i).getTextureName().equals(txName)) {
                    if (striangles != null) {
                        smdtriangles = striangles.toArray(new SmdTriangle[]{});
                        smesh = new SmdMesh();
                        smesh.setTriangles(smdtriangles);
                        smesh.setTextureName(txName);
                        this.meshes.add(smesh);
                    }
                    txName = triangles.get(i).getTextureName();
                    striangles = new ArrayList<SmdTriangle>();
                }
                striangles.add(triangles.get(i));
            }
            if (striangles != null) {//last mesh
                smdtriangles = striangles.toArray(new SmdTriangle[]{});
                smesh = new SmdMesh();
                smesh.setTriangles(smdtriangles);
                smesh.setTextureName(txName);
                this.meshes.add(smesh);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return this.meshes;
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

    private void buildBoneInfluence(SmdVertex vert) {
        try {
//            SmdBone bone = this.bonesMap.get(vert.getBoneIndex());
//            if (bone != null) {
//                bone.addVertex(vert);
//                this.bonesMap.put(vert.getBoneIndex(), bone);
//            }
            this.bonesMap.get(vert.getBoneIndex()).addVertex(vert);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
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
