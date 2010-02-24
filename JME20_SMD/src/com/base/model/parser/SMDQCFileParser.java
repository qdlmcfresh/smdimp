/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parser;

import com.base.model.smd.SmdAnimation;
import com.base.model.smd.SmdHbox;
import com.base.model.smd.SmdModel;
import com.jme.math.Vector3f;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that parses .qc file. Recovers model info, model meshes, animations,
 * hit boxes, attachments  ...
 * @see http://developer.valvesoftware.com/wiki/Qc
 * @author serser
 */
public class SMDQCFileParser extends SMDParser {

    private static final String MODELNAME_RESERVATE = "$modelname";
    private static final String MODELPATH_RESERVATE = "$cd";
    private static final String MODELTEXTUREPATH_RESERVATE = "$cdtexture";
    private static final String MODELSCALE_RESERVATE = "$scale";
    private static final String TEXTUREPATH_RESERVATE = "$cdtexture";
    private static final String MODELATTACHMENT_RESERVATE = "$attachment";
    private static final String MODELCONTROLLER_RESERVATE = "$controller";
    private static final String MODELHITBOX_RESERVATE = "$hbox";
    private static final String MODELBODYGROUP_RESERVATE = "$bodygroup";
    private static final String MODELBODY_RESERVATE = "$body";
    private static final String MODELSEQUENCE_RESERVATE = "$sequence";
    private String modelname;
    private int geomIndex = 0;
    private float scale;
    private String filePath;
    private String folderPath;
    private ArrayList<SmdAnimation> animations;
    private ArrayList<SmdHbox> hitBoxes;
    private ArrayList<String> modelMeshFilters;
    private boolean isBodyGroup = false;
    private boolean isblendAnimation = false;
    private static final Logger logger = Logger.getLogger(
            SMDQCFileParser.class.getName());

    /**
     * Instantiates new .qc file parser.
     * @param path String with .qc file path.
     */
    public SMDQCFileParser(String path) {
        this.filePath = path;
        this.folderPath = new File(path).getParent().concat(File.separator);
    }

    /**
     * Method that parses .qc file, recovers model meshes, animations and
     * hit boxes.
     * @return ArrayList with diferents models that contains .qc file.
     */
    public ArrayList<SmdModel> parse() {
        ArrayList<SmdModel> meshes = null;
        try {
            logger.log(Level.INFO, "Init parsing SmdNode from : " + filePath);
            meshes = new ArrayList<SmdModel>();
            animations = new ArrayList<SmdAnimation>();
            hitBoxes = new ArrayList<SmdHbox>();
            lines = readFile(new File(filePath).toURI().toURL());
            String line;
            String[] lineTokens;
            String type = "";
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line != null && !line.equals("")) {
                    lineTokens = line.split(SPLITCHAR);
                    if (line.startsWith(MODELSCALE_RESERVATE)) {
                        scale = getScale(line);
                    } else if (line.startsWith(MODELBODYGROUP_RESERVATE)) {
                        if (lineTokens.length > 0) {//get the last element
                            String meshname = lineTokens[lineTokens.length - 1].replaceAll("\"", "");
                            if (!checkModelMeshFilter(meshname)) {
                                isBodyGroup = true;
                                if (meshname.equals("body") || meshname.equals("studio")) {
                                    type = "body";
                                } else {
                                    type = "part";
                                }
                            }
                        }
                    } else if (isBodyGroup) {
                        if (line.startsWith("studio")) {
                            if (lineTokens.length > 0) {//get the last element
                                String meshname = lineTokens[lineTokens.length - 1].replaceAll("\"", "");
                                meshes.add(getSmdModel(folderPath.concat(
                                        checkIsRelative(meshname)).concat(".smd"), type));
                                isBodyGroup = false;
                            }
                        }
                    } else if (line.startsWith(MODELBODY_RESERVATE)) {
                        if (lineTokens.length > 0) {//get the last element
                            String meshname = lineTokens[lineTokens.length - 1].replaceAll("\"", "");
                            meshes.add(
                                    getSmdModel(folderPath.concat(
                                    meshname).concat(".smd"), "body"));
                        }
                    } else if (line.startsWith(MODELSEQUENCE_RESERVATE) || isblendAnimation) {
                        SmdAnimation panimation = this.getSmdAnimation(line);
                        if (panimation != null) {
                            this.animations.add(panimation);
                        }
                    } else if (line.startsWith(MODELHITBOX_RESERVATE)) {
                        SmdHbox hbox = this.buildSmdHitBox(line);
                        this.hitBoxes.add(hbox);
                    }
                }
            }
            //TODO animations should include in main model to all submeshes.
            for (int i = 0; i < meshes.size(); i++) {
                meshes.get(i).setAnimations(animations);
                meshes.get(i).setHitBoxes(hitBoxes);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error parsing SmdNode : ", Ex);
        }
        return meshes;
    }

    /**
     * Method that recovers the model scale-
     * @param line
     * @return float with the model scale
     */
    private float getScale(String line) {
        float mscale = 1.0f;
        String[] lparts;
        try {
            lparts = line.split(SPLITCHAR);
            if (lparts.length > 0) {
                mscale = Float.parseFloat(lparts[1]);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering model scale.", Ex);
        }
        return mscale;
    }

    /**
     * Method that recovers SmdModel, receives the .smd path that contains the
     * model and the type.SMDModelParser class needs geometric index
     * to assing mesh to vertex.
     * @param path : String with the path of the .smd file that contains the
     * model.
     * @param type : String with the model type.
     * @return builded SmdModel.
     */
    private SmdModel getSmdModel(String path, String type) {
        SmdModel model = null;
        try {
            SMDModelParser psmdModel = new SMDModelParser(path, type);
            psmdModel.setGeomIndex(geomIndex);
            logger.log(Level.INFO, "GEOM-INDEX : " + geomIndex);
            model = psmdModel.parse();
            this.geomIndex = geomIndex + model.getMeshes().size();
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering model from : " + path, Ex);
        }
        return model;
    }

    /**
     * Method that recovers an animation from smd file, analizes the text line
     * and buils SmdAnimation object parsing smd animation file @see SMDAnimationParser
     * @param line : String with sequence line from qc file.
     * @return : SmdAnimation build object.
     */
    private SmdAnimation getSmdAnimation(String line) {
        SMDAnimationParser anparser = null;
        SmdAnimation animation = null;
        String[] lparts;
        File aFile = null;
        try {
            lparts = line.split(SPLITCHAR);
            lparts = this.clean(lparts, "\"");
            if (!isblendAnimation) {
                if (lparts.length == 8) {//contains simple animation
                    aFile = new File(folderPath.concat(File.separator).concat(
                            lparts[2].concat(".smd")));
                } else if (lparts.length == 3) {//Main blend animation
                    if (lparts[2].equals(GROUP_IN)) {
                        animation = new SmdAnimation(lparts[1], true);
                        isblendAnimation = true;
                        return animation;
                    }
                } else {
                    aFile = new File(folderPath.concat(File.separator).concat(
                            lparts[2].concat(".smd")));
                }
                if (aFile != null) {
                    if (aFile.exists()) {
                        anparser = new SMDAnimationParser(aFile.getAbsolutePath());
                        animation = anparser.parse();
                        animation.setName(lparts[1]);
                        animation.setFps(getFPS(lparts));
                    } else {
                        logger.log(Level.SEVERE, "Error, animation file : "
                                + lparts[1] + " not found.");
                    }
                }
            } else {//blend animation
                if (lparts.length == 1) {
                    if (lparts[0].equals(GROUP_OUT)) {
                        isblendAnimation = false;
                    } else {
                        aFile = new File(folderPath.concat(File.separator).concat(
                                lparts[0].concat(".smd")));
                    }
                } else {
                    this.animations.get(this.animations.size() - 1).setFps(getFPS(lparts));
                }
                if (aFile != null) {
                    if (aFile.exists()) {
                        //stores blendAnimation in last animation added
                        anparser = new SMDAnimationParser(aFile.getAbsolutePath());
                        animation = anparser.parse();
                        animation.setName(lparts[0]);
                        this.animations.get(
                                this.animations.size() - 1).addSmdAnimation(animation);
                        return null;
                    }
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering animation from : "
                    + line, Ex);
        } finally {
            anparser = null;
        }
        return animation;
    }

    /**
     * Method that builds a hit box from qc File;
     * @param line : String line that contains SmdHbox
     * @return builded SmdHbox.
     */
    private SmdHbox buildSmdHitBox(String line) {
        SmdHbox hbox = null;
        String[] lparts;
        try {
            lparts = line.split(SPLITCHAR);
            lparts = this.clean(lparts, "\"");
            hbox = new SmdHbox(Integer.parseInt(lparts[1]), lparts[2],
                    new Vector3f(Float.parseFloat(lparts[3]),
                    Float.parseFloat(lparts[4]), Float.parseFloat(lparts[5])),
                    new Vector3f(Float.parseFloat(lparts[6]),
                    Float.parseFloat(lparts[7]), Float.parseFloat(lparts[8])));
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering hitBox from : "
                    + line, Ex);
        }
        return hbox;
    }

    /**
     * Method that search for "fps" tag in a line and returns a float with the
     * animation fps.
     * @param lparts : String with the line to parse.
     * @return float with animation fps.
     */
    private float getFPS(String[] lparts) {
        float fps = 23f;
        try {
            for (int i = 0; i < lparts.length; i++) {
                if (lparts[i].equals("fps")) {
                    fps = Float.parseFloat(lparts[i + 1]);
                    break;
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error recovering fps.", Ex);
        }
        return fps;
    }

    /**
     * Method that check if a mesh it's filtered.
     * @param modelMesh : name of the mesh to check.
     * @return boolean : isFiltered.
     */
    private boolean checkModelMeshFilter(String modelMesh) {
        boolean isFiltered = false;
        try {
            if (modelMeshFilters != null && modelMeshFilters.size() > 0) {
                isFiltered = this.modelMeshFilters.contains(modelMesh);
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error checking mesh filter.", Ex);
        }
        return isFiltered;
    }

    /**
     * Method that recovers the name of the file from path
     * @param path : String with the path of the file.
     * @return String : file name.
     */
    private String checkIsRelative(String path) {
        String mname = "";
        try {
            mname = new File(path).getName();
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, recovering the name of the path.", Ex);
        }
        return mname;
    }

    /**
     * Method that adds a mesh name to filter.
     * @param bfilter : String with the mesh name to filter.
     */
    public void addModelMeshFilter(String bfilter) {
        if (this.modelMeshFilters == null) {
            this.modelMeshFilters = new ArrayList<String>();
            this.modelMeshFilters.add(bfilter);
        }
    }

    public static void main(String[] args) {
        try {
            SMDQCFileParser pqc = new SMDQCFileParser("C:\\JME\\Models\\player\\eightball\\eightball.qc");
            pqc.parse();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
