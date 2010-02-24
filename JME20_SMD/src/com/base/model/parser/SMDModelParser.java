/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parser;

import com.base.model.smd.SmdSkeleton;
import com.base.model.smd.SmdVertex;
import com.base.model.smd.SmdTriangle;
import com.base.model.smd.SmdMesh;
import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdBone;
import com.base.model.smd.SmdModel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author serser
 */
public class SMDModelParser extends SMDParser {

    private static final String VERSION_TAG = "version";
    private static final String BONE_TAG = "nodes";
    private static final String SKELETON_TAG = "skeleton";
    private static final String TRIANGLES_TAG = "triangles";
    private static final String TIME_TAG = "time";
    private static final String END_TAG = "end";
    private String filePath;
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
    private SmdTriangle triangle;
    private String textureName = "";
    private int geomIndex = 0;
    private String type;
    private static final Logger logger = Logger.getLogger(
            SMDModelParser.class.getName());

    public SMDModelParser(String path, String type) {
        try {
            this.type = type;
            this.filePath = path;
            this.bonesMap = new HashMap<Integer, SmdBone>();
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error parsing .smd File : ", Ex);
        }
    }

    /**
     * Method that parses .smd file, recovers the triangles, meshes
     * and skeleton.
     * @return builded SmdModel.
     */
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
            lines = readFile(new File(filePath).toURI().toURL());
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
            logger.log(Level.SEVERE, "Error parsing .smd file " + filePath
                    + " : ", Ex);
        }
        return model;
    }

    public int getGeomIndex() {
        return geomIndex;
    }

    public void setGeomIndex(int geomIndex) {
        this.geomIndex = geomIndex;
    }

    /**
     * Method that recovers model version from line tokens.
     * @param line String with the line to parse.
     * @return String with model version.
     */
    private String getVersion(String line) {
        String version = "";
        String[] lparts;
        try {
            lparts = line.split(SPLITCHAR);
            if (lparts.length > 0) {
                version = lparts[1];
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering version from line"
                    + " tokens : " + line, Ex);
        }
        return version;
    }

    /**
     * Method that recovers the skeleton from .smd file,
     * @param line String with the line to parse.
     */
    private void getBones(String line) {
        SmdBone bone;
        String[] lparts;
        try {
            if (!line.startsWith(BONE_TAG)) {
                lparts = line.split(SPLITCHAR);
                lparts = clean(lparts, 3);
                bone = new SmdBone(new Integer(lparts[0]).intValue(), lparts[1],
                        new Integer(lparts[2]).intValue());
                bones.add(bone);
                bonesMap.put(bone.getBoneIndex(), bone);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering bones from line"
                    + " tokens : " + line, Ex);
        }
    }

    /**
     * Method that recovers bone position (rotation and traslation) from
     * initial pose, stores bone positions in a list.
     * @param line String with the line to parse.
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
                bonePos.setPosition(Float.parseFloat(lparts[1]),
                        Float.parseFloat(lparts[2]), Float.parseFloat(lparts[3]));
                bonePos.setRotation(Float.parseFloat(lparts[4]),
                        Float.parseFloat(lparts[5]), Float.parseFloat(lparts[6]));
                bonePositions.add(bonePos);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering bone positions from line"
                    + " tokens : " + line, Ex);
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
     * Method that recovers the triangles and stores vertex bone influence.
     * @param line : String with text line to analize.
     * @param geomIndex : mesh index that triangles composes.
     */
    private void getTriangles(String[] splitLine, int geomIndex) {
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
            logger.log(Level.SEVERE, "Error recovering triangle from geomIndex : "
                    + geomIndex, Ex);
        }
    }

    /**
     * Method that recovers a bone from bones map with vertex bone index
     * and assigns the vertex.
     * @param vert SmdVertex to add.
     */
    private void buildBoneInfluence(SmdVertex vert) {
        try {
            this.bonesMap.get(vert.getBoneIndex()).addVertex(vert);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error adding bone influence from " +
                    "geomIndex : " + geomIndex, Ex);
        }
    }

    /**
     * Method that builds SmdSkeleton generates SmdBonePosition from parsed
     * info.
     * @return builded SmdSkeleton.
     */
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
            logger.log(Level.SEVERE, "Error building skeleton from " +
                    "geomIndex : " + geomIndex, Ex);
        }
        return smdskeleton;
    }

    /**
     * Method that builds submeshes, stores a submesh in a list of SmdMesh
     * when triangle texture changes.
     * @return ArrayList<SmdMesh> with submeshes.
     */
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
            logger.log(Level.SEVERE, "Error building meshes from geomIndex : "
                    + geomIndex, Ex);
        }
        return this.meshes;
    }
}
