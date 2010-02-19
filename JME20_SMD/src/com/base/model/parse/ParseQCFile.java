/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parse;

import com.base.model.smd.SmdAnimation;
import com.base.model.smd.SmdHbox;
import com.base.model.smd.SmdModel;
import com.jme.math.Vector3f;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author serser
 */
public class ParseQCFile {

    private ArrayList<String> lines;
    private static String MODELNAME_RESERVATE = "$modelname";
    private static String MODELPATH_RESERVATE = "$cd";
    private static String MODELTEXTUREPATH_RESERVATE = "$cdtexture";
    private static String MODELSCALE_RESERVATE = "$scale";
    private static String TEXTUREPATH_RESERVATE = "$cdtexture";
    private static String MODELATTACHMENT_RESERVATE = "$attachment";
    private static String MODELCONTROLLER_RESERVATE = "$controller";
    private static String MODELHITBOX_RESERVATE = "$hbox";
    private static String MODELBODYGROUP_RESERVATE = "$bodygroup";
    private static String MODELBODY_RESERVATE = "$body";
    private static String MODELSEQUENCE_RESERVATE = "$sequence";
    private static String SPLITCHAR = " ";
    private static String GROUP_IN = "{";
    private static String GROUP_OUT = "}";
    private String modelname;
    private float scale;
    private String filePath;
    private String folderPath;
    private ArrayList<SmdAnimation> animations;
    private ArrayList<SmdHbox> hitBoxes;
    private ArrayList<String> bodyFilters;
    private boolean isBodyGroup = false;
    private boolean isblendAnimation = false;

    public ParseQCFile(String path) {
        this.filePath = path;
        this.folderPath = new File(path).getParent().concat(File.separator);
    }

    /**
     * Method that parses qc file
     * @return
     */
    public ArrayList<SmdModel> parse() {
        ArrayList<SmdModel> meshes = null;
        try {
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
                            if (!checkBodyFilter(meshname)) {
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
                                meshes.add(getSmdModel(folderPath.concat(checkIsRelative(meshname)).concat(".smd"), type));
                                isBodyGroup = false;
                            }
                        }
                    } else if (line.startsWith(MODELBODY_RESERVATE)) {
                        if (lineTokens.length > 0) {//get the last element
                            String meshname = lineTokens[lineTokens.length - 1].replaceAll("\"", "");
                            meshes.add(getSmdModel(folderPath.concat(meshname).concat(".smd"), "body"));
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
            Ex.printStackTrace();
        }
        return meshes;
    }

    private float getScale(String line) {
        float mscale = 1.0f;
        String[] lparts;
        try {
            lparts = line.split(SPLITCHAR);
            if (lparts.length > 0) {
                mscale = Float.parseFloat(lparts[1]);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return mscale;
    }

    private SmdModel getSmdModel(String path, String type) {
        SmdModel model = null;
        try {
            ParseSMDModel psmdModel = new ParseSMDModel(path, type);
            model = psmdModel.parse();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return model;
    }

    /**
     * Method that recovers an animation from smd file, analizes the text line
     * and buils SmdAnimation object parsing smd animation file @see ParseSMDAnimation
     * @param line : String with sequence line from qc file.
     * @return : SmdAnimation build object.
     */
    private SmdAnimation getSmdAnimation(String line) {
        ParseSMDAnimation anparser = null;
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
                        animation = new SmdAnimation(true);
                        animation.setName(lparts[1]);
                        isblendAnimation = true;
                        return animation;
                    }
                } else {
                    aFile = new File(folderPath.concat(File.separator).concat(
                            lparts[2].concat(".smd")));
                }
                if (aFile != null) {
                    if (aFile.exists()) {
                        anparser = new ParseSMDAnimation(aFile.getAbsolutePath());
                        animation = anparser.parse();
                        animation.setName(lparts[1]);
                        animation.setFps(getFPS(lparts));
                    } else {
                        System.err.println(lparts[1] + " animation file not found");
                    }
                }
            } else {//is blend animation
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
                        anparser = new ParseSMDAnimation(aFile.getAbsolutePath());
                        animation = anparser.parse();
                        animation.setName(lparts[0]);
                        this.animations.get(this.animations.size() - 1).addSmdAnimation(animation);
                        return null;
                    }
                }
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
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
            Ex.printStackTrace();
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
            Ex.printStackTrace();
        }
        return fps;
    }

    private String[] clean(String[] original, String chartodel) {
        ArrayList<String> cleaned = null;
        try {
            cleaned = new ArrayList<String>();
            int j = 0;
            boolean sequence = false;
            for (int i = 0; i < original.length; i++) {
                if (!original[i].equals("") && !original[i].equals(" ")) {
                    if (original[i].startsWith("\"") && !original[i].endsWith("\"")) {
                        cleaned.add(original[i]);
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        sequence = true;
                    } else if (!original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned.add(j, cleaned.get(j).concat(original[i]));
                        cleaned.remove(j + 1);
                        sequence = false;
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        j++;
                    } else if (original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned.add(original[i]);
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        j++;
                    } else {
                        if (sequence) {
                            cleaned.add(j, cleaned.get(j).concat(original[i]));
                            cleaned.remove(j + 1);
                            if (chartodel != null) {
                                cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                                cleaned.remove(j + 1);
                            }
                        } else {
                            cleaned.add(original[i]);
                            if (chartodel != null) {
                                cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                                cleaned.remove(j + 1);
                            }
                            j++;
                        }
                    }
                }
            }
            original = new String[cleaned.size()];
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }

        return cleaned.toArray(original);
    }

//        private String[] clean(String[] original, int positions) {
//        String[] cleaned = null;
//        try {
//            cleaned = new String[positions];
//            int j = 0;
//            boolean sequence = false;
//            for (int i = 0; i < original.length; i++) {
//                if (!original[i].equals("")) {
//                    if (original[i].startsWith("\"") && !original[i].endsWith("\"")) {
//                        cleaned[j] = original[i];
//                        sequence = true;
//                    } else if (!original[i].startsWith("\"") && original[i].endsWith("\"")) {
//                        cleaned[j] = cleaned[j].concat(original[i]);
//                        sequence = false;
//                        j++;
//                    } else if (original[i].startsWith("\"") && original[i].endsWith("\"")) {
//                        cleaned[j] = original[i];
//                        j++;
//                    } else {
//                        if (sequence) {
//                            cleaned[j] = cleaned[j].concat(original[i]);
//                        } else {
//                            cleaned[j] = original[i];
//                            j++;
//                        }
//                    }
//                }
//            }
//        } catch (Exception Ex) {
//            Ex.printStackTrace();
//        }
//
//        return cleaned;
//    }

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

    private boolean checkBodyFilter(String bodyMesh) {
        boolean isFiltered = false;
        try {
            if (bodyFilters != null && bodyFilters.size() > 0) {
                isFiltered = this.bodyFilters.contains(bodyMesh);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return isFiltered;
    }

    private String checkIsRelative(String path) {
        String mname = "";
        try {
            mname = new File(path).getName();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return mname;
    }

    public void addBodyFilter(String bfilter) {
        if (this.bodyFilters == null) {
            this.bodyFilters = new ArrayList<String>();
            this.bodyFilters.add(bfilter);
        }
    }

    public static void main(String[] args) {
        try {
            ParseQCFile pqc = new ParseQCFile("C:\\JME\\Models\\player\\eightball\\eightball.qc");
            pqc.parse();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
