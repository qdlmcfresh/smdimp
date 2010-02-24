/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.jme;

import com.base.model.parser.SMDQCFileParser;
import com.base.model.smd.SmdSkeleton;
import com.base.model.smd.SmdVertex;
import com.base.model.smd.SmdTriangle;
import com.base.model.smd.SmdMesh;
import com.base.model.smd.SmdAnimation;
import com.base.model.smd.SmdBonePosition;
import com.base.model.smd.SmdBone;
import com.base.model.smd.SmdModel;
import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.BoneTransform;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that represents smd model node in jme, extends from SkinNode,
 * the node should have bones and animations.
 * @author serser
 */
public class SMDNode extends SkinNode {

    private DisplaySystem display;
    private AnimationController controller;
    private HashMap<Bone, ArrayList<Matrix4f>> btransforms;
    private String textureURL;
    private Bone[] bones = null;
    private static final Logger logger = Logger.getLogger(
            SMDNode.class.getName());

    public SMDNode(String resourceQCFile, DisplaySystem display) {
        super();
        this.display = display;
        this.textureURL = new File(resourceQCFile).getParent();
        logger.log(Level.INFO, "Init, SmdNode from : " + resourceQCFile);
        this.getSmdNode(resourceQCFile);
        logger.log(Level.INFO, "Finished OK, SmdNode from : " + resourceQCFile);
        this.setLocalRotation(this.buildMatrix(new Vector3f(((float) (Math.PI
                + Math.PI / 2)), 0, 0)));
    }

    /**
     * Method that returns the animation controller.
     * @return AnimationController for this SkinNode.
     */
    public AnimationController getController() {
        return controller;
    }

    public void update(float time) {
        if (skeleton != null) {
            this.skeleton.updateGeometricState(0, true);
            this.skeleton.update();
            controller.update(time);
        }
    }

    /**
     * Method that creates a SmdNode from qc file, recovers meshes, textures,
     * skeleton, animations. Creates the skeleton and the animation controller.
     * @param resourceQCFile : String with the path of .qc file that contains
     * models data.
     */
    private void getSmdNode(String resourceQCFile) {
        SMDQCFileParser pmodel = null;
        SmdModel model = null;
        ArrayList<SmdModel> meshes = null;
        try {

            pmodel = new SMDQCFileParser(resourceQCFile);
            meshes = pmodel.parse();
            logger.log(Level.INFO, "Parsing Finished, meshes count : " + meshes.size());
            for (int i = 0; i < meshes.size(); i++) {
                model = meshes.get(i);
                for (int j = 0; j < model.getMeshes().size(); j++) {//add the meshes to the node.
                    logger.log(Level.INFO, "Skinning, for mesh : " + model.getName() + i + j);
                    this.addSkin(buildTrimesh(model.getName() + i + j,
                            model.getMeshes().get(j)));
                }
                if (model.getType().equals("body")) {
                    this.setName(model.getName());
                    if (model.getSkeleton() != null) {
                        buildSkeleton(model.getSkeleton());
                        buildAnimationController(model.getAnimations());
                    }
                }
            }
            //add bone influence from mesh vertex list.
            logger.log(Level.INFO, "Adding bone influence");
            for (int j = 0; j < meshes.size(); j++) {
                //for (int i = 0; i < model.getMeshes().size(); i++) {
                //buildBoneInfluence(model.getMeshes().get(i).);
                //}
                buildBoneInfluence(meshes.get(j).getSkeleton().getBones());
            }
            this.normalizeWeights();
            this.regenInfluenceOffsets();
            this.addController(controller);
            controller.setRepeatType(Controller.RT_WRAP);
            //set bounding box
            this.setModelBound(new BoundingBox());
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating SmdNode : " + meshes.size(), Ex);
        } finally {
            pmodel = null;
            model = null;
            meshes = null;
        }
    }

    /**
     * Method that builds a mesh from SmdMesh.
     * @param name : Mesh name.
     * @param mesh : SmdMesh that contains the vertex, normals and texture
     * coordinates.
     * @return builded Trimesh.
     */
    private TriMesh buildTrimesh(String name, SmdMesh mesh) {
        FloatBuffer verts = null;
        FloatBuffer normals = null;
        TexCoords coords = null;
        IntBuffer index = null;
        ArrayList<Vector3f> vertsList = null;
        ArrayList<Vector3f> normList = null;
        ArrayList<Vector2f> textCoordList = null;
        int[] triangleIndex;
        TriMesh msh = null;
        String tname;
        try {
            vertsList = new ArrayList();
            normList = new ArrayList();
            textCoordList = new ArrayList();
            tname = mesh.getTextureName();
            //process triangles.
            SmdTriangle[] triangles = mesh.getTriangles();
            SmdTriangle triangle;
            logger.log(Level.INFO, "Trying to create Trimesh : " + name);
            for (int i = 0; i < triangles.length; i++) {
                triangle = triangles[i];
                for (int j = 0; j < triangle.getVertsSize(); j++) {
                    vertsList.add(triangle.getSmdVertex(j).getPosition());
                    normList.add(triangle.getSmdVertex(j).getNormals());
                    textCoordList.add(triangle.getSmdVertex(j).getTextCoord());
                }
            }
            triangleIndex = new int[vertsList.size()];
            for (int i = 0; i < triangleIndex.length; i++) {
                triangleIndex[i] = i;
            }
            Vector3f[] v = new Vector3f[vertsList.size()];
            Vector3f[] n = new Vector3f[normList.size()];
            Vector2f[] c = new Vector2f[textCoordList.size()];
            logger.log(Level.INFO, "vertex number : " + v.length);
            verts = BufferUtils.createFloatBuffer(vertsList.toArray(v));
            normals = BufferUtils.createFloatBuffer(normList.toArray(n));
            coords = new TexCoords(BufferUtils.createFloatBuffer(textCoordList.toArray(c)));
            index = BufferUtils.createIntBuffer(triangleIndex);
            msh = new TriMesh(name, verts, normals, null, coords, index);
            logger.log(Level.INFO, "Trimesh : " + name + " created OK.");
            this.setTexture(msh, tname);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating Trimesh : ", Ex);
        } finally {
            vertsList = null;
            normList = null;
            textCoordList = null;
            triangleIndex = null;
        }
        return msh;
    }

    /**
     * Method that builds animation controller with bone animations. Finally add
     * the animation controller to this SkinNode.
     * @param animations : SmdAnimations to build the animation controller.
     */
    private void buildAnimationController(ArrayList<SmdAnimation> animations) {
        controller = null;
        try {
            controller = new AnimationController();
            for (int i = 0; i < animations.size(); i++) {
                if (animations.get(i).isBlendAnimation()) {
                    logger.log(Level.INFO, "Adding blend animation.");
                    for (int j = 0; j < animations.get(i).getSmdAnimationSize(); j++) {
                        controller.addAnimation(buildBoneAnimation(animations.get(i).getSmdAnimation(j),
                                this.getSkeleton()));
                    }
                } else {
                    controller.addAnimation(buildBoneAnimation(animations.get(i),
                            this.getSkeleton()));
                }
            }
            logger.log(Level.INFO, "Animation controller created OK, animation "
                    + "number : " + controller.getAnimations().size() + ".");
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating AnimationController : ", Ex);
        }
    }

    /**
     * Method that creates a boneAnimation, the method receives an SmdAnimation
     * @see SmdAnimation that contains the bone strucure and bone
     * traslation/rotation for each time. Creates a bone Transformation Map that
     * holds the bone and the matrix position for each time for that bone.
     * @param animation : SmdAnimation parsed from Smd animation file, contains the
     *                    bone strucure and bone traslation/rotation for each time.
     * @param skeleton builded bone skeleton.
     * @return BoneAnimation.
     */
    private BoneAnimation buildBoneAnimation(SmdAnimation animation, Bone skeleton) {
        BoneAnimation banimation = null;
        try {
            logger.log(Level.INFO, "Trying to create animation : "
                    + animation.getName());
            banimation = new BoneAnimation(animation.getName(), skeleton,
                    animation.getSkeletonPositions().length);
            btransforms = new HashMap<Bone, ArrayList<Matrix4f>>();
            for (int i = 0; i < animation.getSkeletonPositions().length; i++) {
                buildBoneTransformMap(animation.getSkeletonPositions()[i], skeleton, i);
            }
            Set boneSet = this.btransforms.keySet();
            Iterator<Bone> biterator = boneSet.iterator();
            Bone bkey = null;
            while (biterator.hasNext()) {
                bkey = biterator.next();
                banimation.addBoneTransforms(buildBoneTransform(bkey,
                        this.btransforms.get(bkey)));
            }
            logger.log(Level.INFO, "Bone transformation added.");
            banimation.setTimes(getTimes(animation.getFps(),
                    animation.getSkeletonPositions().length));
            logger.log(Level.INFO, "Added times.Animation fps : "
                    + animation.getFps());
            int[] interpolationTypes = new int[animation.getSkeletonPositions().length];
            for (int i = 0; i < interpolationTypes.length; i++) {
                interpolationTypes[i] = 0;
            }
            banimation.setInterpolationTypes(interpolationTypes);
            banimation.setInitialFrame(0);
            logger.log(Level.INFO, "Total animation frames : "
                    + (animation.getSkeletonPositions().length - 1));
            banimation.setEndFrame(animation.getSkeletonPositions().length - 1);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating Animation "
                    + animation.getName() + " : ", Ex);
        }
        return banimation;
    }

    /**
     * Method that assign the transformation for a bone, creates a hashmap with
     * bones as keys and array of matrix as value.
     * @param skeletonPosition
     * @param skeleton : skeleton bone structure.
     * @param time : transformation time.
     */
    private void buildBoneTransformMap(SmdSkeleton skeletonPosition, Bone skeleton, int time) {
        Bone btoTransform = null;
        try {
            for (int j = 0; j < skeletonPosition.getPositions().length; j++) {
                if (j == 0) {//rootBone
                    addTransformToMap(skeletonPosition.getPositions()[0], skeleton, time);
                } else {
                    btoTransform = (Bone) skeleton.getChild(skeletonPosition.getBones()[j].getBoneName());
                    addTransformToMap(skeletonPosition.getPositions()[j], btoTransform, time);
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating BoneTransformMap : ", Ex);
        }
    }

    /**
     * Method that adds a transform to the bone transformation list. Creates a
     * a Bind Matrix from SmdBonePosition.
     * @param sboneTrans : Object that stores the bone transformation.
     * @param bone : bone that affect transformation.
     * @param time : frame of the bone transform.
     */
    private void addTransformToMap(SmdBonePosition sboneTrans, Bone bone, int time) {
        try {
            ArrayList<Matrix4f> transforms = this.btransforms.get(bone);
            if (transforms == null) {
                transforms = new ArrayList<Matrix4f>();
            }
            transforms.add(time, this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
            this.btransforms.put(bone, transforms);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, adding BoneTransformMap : ", Ex);
        }
    }

    /**
     * Method that builds bone transform.
     * @param boneToTransform : bone to add transform.
     * @param relPositions : ArrayList with Matrix4f positions of the bone.
     * @return BoneTransform : builded BoneTransform.
     */
    private BoneTransform buildBoneTransform(Bone boneToTransform,
            ArrayList<Matrix4f> relPositions) {
        BoneTransform bTransform = null;
        try {
            bTransform = new BoneTransform(boneToTransform, relPositions.size());
            for (int i = 0; i < relPositions.size(); i++) {
                bTransform.setRotation(i, relPositions.get(i).toRotationQuat());
                bTransform.setTranslation(i, relPositions.get(i).toTranslationVector());
            }
            bTransform.setCurrentFrame(0);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating BoneTransform : ", Ex);
        }
        return bTransform;
    }

    /**
     * Method that builds float[] with animation time position. Calculates
     * the time between frames.
     * @param fps : float with frames per second of the animation.
     * @param positions : int animation positions number.
     * @return float[] with animation time position.
     */
    private float[] getTimes(float fps, int positions) {
        float[] times = null;
        float time;
        try {
            times = new float[positions];
            time = (1f / (fps));
            for (int i = 0; i < times.length; i++) {
                if (i > 0) {
                    times[i] = time + times[i - 1];
                } else {
                    times[i] = 0;
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, creating Times : ", Ex);
        }
        return times;
    }

    /**
     * Method that applies a texture to a mesh. builds a texture path with qc file
     * and texture name.
     * @param mesh : Trimesh to apply texture.
     * @param textureName : String with the texture name to apply.
     */
    private void setTexture(TriMesh mesh, String textureName) {
        File textFile;
        try {
            logger.log(Level.INFO, "Trying to apply texture : " + textureName
                    + " to mesh : " + mesh.getName());
            textFile = new File(textureURL.concat(File.separator).concat(textureName));
            TextureState ts = display.getRenderer().createTextureState();
            Texture t1 = TextureManager.loadTexture(textFile.toURI().toURL(),
                    Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear);
            ts.setTexture(t1, 0);
            mesh.setRenderState(ts);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, applying texture to mesh.", Ex);
        }

    }

    /**
     * Method that builds the skeleton
     * @param skeleton : SmdSkeleton that contain the skeleton
     *                   and the bones.
     */
    private void buildSkeleton(SmdSkeleton skeleton) {
        Bone rootBone = null;
        SmdBone[] sbones = null;
        SmdBonePosition[] bindPosition = null;
        bones = null;
        try {
            sbones = skeleton.getBones();
            bindPosition = skeleton.getPositions();
            bones = new Bone[sbones.length];
            Bone bone;
            for (int i = 0; i < sbones.length; i++) {
                bone = new Bone(sbones[i].getBoneName());
                Matrix4f bmatrix;
                bmatrix = getBindMatrix(bindPosition[i].getPosition(), bindPosition[i].getRotation());
                if (sbones[i].getBoneParent() != -1) {
                    bone.setBindMatrix(bones[sbones[i].getBoneParent()].getBindMatrix().mult(bmatrix));
                    bone.setLocalRotation(bmatrix.toRotationQuat());
                    bone.setLocalTranslation(bmatrix.toTranslationVector());
                    bones[sbones[i].getBoneParent()].attachChild(bone);
                } else {
                    bone.setBindMatrix(bmatrix);
                    bone.setLocalRotation(bmatrix.toRotationQuat());
                    bone.setLocalTranslation(bmatrix.toTranslationVector());
                }
                bones[i] = bone;
            }
            rootBone = bones[0];
            rootBone.updateGeometricState(0, true);
            this.setSkeleton(rootBone);
            this.attachChild(rootBone);

        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, building Skeleton.", Ex);
        }
    }

    /**
     * Method that asigns vertex to the bones to determine the bone influence.
     * @param sbones : SmdBones with influence data (geometric Intex, vetex).
     */
    private void buildBoneInfluence(SmdBone[] sbones) {
        ArrayList<SmdVertex> bv = null;
        try {
            for (int i = 0; i < sbones.length; i++) {
                bv = sbones[i].getVertexInfluence();
                if (bv != null) {
                    for (int j = 0; j < bv.size(); j++) {
                        try {
                            this.addBoneInfluence(bv.get(j).getGeomIndex(),
                                    bv.get(j).getVertIndex(),
                                    bones[i],
                                    1f);
                        } catch (Exception Ex) {
                            logger.log(Level.INFO, "FAILS : "
                                    + bv.get(j).getGeomIndex() + " "
                                    + sbones[i].getBoneName() + " "
                                    + bv.get(j).getVertIndex());
                            logger.log(Level.SEVERE, "Error, adding BoneInfluence.", Ex);
                        }
                        //logger.log(Level.INFO, "OK" + bv.get(j).getGeomIndex()
                        //        + " " + sbones[i].getBoneName());
                    }
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, building BoneInfluence.", Ex);
        }
    }

    /**
     * Method that creates bind matrix from traslation vector and rotation.
     * @param tras : Vector3f with traslation.
     * @param rot : Vector3f with rotation.
     * @return Matrix4f : builded matrix4f.
     */
    private Matrix4f getBindMatrix(Vector3f tras, Vector3f rot) {
        Matrix4f bmatrix = null;
        Quaternion q = null;
        try {
            bmatrix = new Matrix4f();
            bmatrix.setIdentity();
            bmatrix.setTranslation(tras);
            q = buildQuat(rot);
            bmatrix.setRotationQuaternion(q);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, building BindMatrix.", Ex);
        }
        return bmatrix;
    }

    /**
     * Method that creates a Quaternion from Vector3f with axis rotation.
     * @param rot : Vector3f with axis rotation.
     * @return Quaternion : builded Quaternion.
     */
    private Quaternion buildQuat(Vector3f rot) {
        Quaternion q = null;
        try {
            q = new Quaternion();
            q.fromRotationMatrix(this.buildMatrix(rot));
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, building Quaternion from rotation"
                    + " Matrix.", Ex);
        }
        return q;
    }

    /**
     * Method that creates rotation Matrix3f from Vector3f with axis rotation.
     * @param rot : Vector3f with axis rotation.
     * @return Matrix3f : builded rotation Matrix3f.
     */
    private Matrix3f buildMatrix(Vector3f rot) {
        Matrix3f m1 = null;
        Matrix3f m2 = null;
        Matrix3f m3 = null;
        try {
            m1 = new Matrix3f();
            m1.fromAngleAxis(rot.x, Vector3f.UNIT_X);
            m2 = new Matrix3f();
            m2.fromAngleAxis(rot.y, Vector3f.UNIT_Y);
            m3 = new Matrix3f();
            m3.fromAngleAxis(rot.z, Vector3f.UNIT_Z);
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error, building rotation Matrix3f from "
                    + "rotation vector.", Ex);
        }
        return m3.mult(m2).mult(m1);
    }

    public static void main(String[] args) {
        try {
            SMDNode node = new SMDNode("C:\\JME\\Models\\player\\Molly\\Molly.qc", null);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
