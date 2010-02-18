/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.jme;

import com.base.model.parse.ParseQCFile;
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

/**
 *
 * @author serser
 */
public class SmdNode extends SkinNode {

    private DisplaySystem display;
    private AnimationController controller;
    private HashMap<Bone, ArrayList<Matrix4f>> btransforms;
    private String textureURL;

    public SmdNode(String resourceQCFile, DisplaySystem display) {
        super();
        this.display = display;
        this.textureURL = new File(resourceQCFile).getParent();
        getSmdNode(resourceQCFile);
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

    private void getSmdNode(String resourceQCFile) {
        ParseQCFile pmodel = null;
        SmdModel model = null;
        ArrayList<SmdModel> meshes = null;
        try {
            pmodel = new ParseQCFile(resourceQCFile);
            pmodel.addBodyFilter("flag");
            meshes = pmodel.parse();
            for (int i = 0; i < meshes.size(); i++) {
                model = meshes.get(i);
                for (int j = 0; j < model.getMeshes().size(); j++) {
                    this.addSkin(buildTrimesh(model.getName() + j, model.getMeshes().get(j)));
                }
                if (model.getType().equals("body")) {
                    this.setName(model.getName());
                    if (model.getSkeleton() != null) {
                        buildSkeleton(model.getSkeleton());
                        buildAnimationController(model.getAnimations());
                    }
                }
            }
            this.addController(controller);
            controller.setActiveAnimation("run2");
            controller.setRepeatType(Controller.RT_WRAP);
            //set bounding box
            this.setModelBound(new BoundingBox());

        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            pmodel = null;
            model = null;
        }
    }

    /**
     * Method that builds a mesh from SmdMesh, contains vertex list
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
        ArrayList<Vector3f> vertsList = new ArrayList();
        ArrayList<Vector3f> normList = new ArrayList();
        ArrayList<Vector2f> textCoordList = new ArrayList();
        int[] triangleIndex;
        TriMesh msh = null;
        String tname = mesh.getTextureName();
        try {
            //process triangles.
            SmdTriangle[] triangles = mesh.getTriangles();
            SmdTriangle triangle;

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
            verts = BufferUtils.createFloatBuffer(vertsList.toArray(v));
            normals = BufferUtils.createFloatBuffer(normList.toArray(n));
            coords = new TexCoords(BufferUtils.createFloatBuffer(textCoordList.toArray(c)));
            index = BufferUtils.createIntBuffer(triangleIndex);
            msh = new TriMesh(name, verts, normals, null, coords, index);
            this.setTexture(msh, tname);
        } catch (Exception Ex) {
            Ex.printStackTrace();
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
                    for (int j = 0; j < animations.get(i).getSmdAnimationSize(); j++) {
                        controller.addAnimation(buildBoneAnimation(animations.get(i).getSmdAnimation(j),
                                this.getSkeleton()));
                    }
                } else {
                    controller.addAnimation(buildBoneAnimation(animations.get(i),
                            this.getSkeleton()));
                }
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
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
            banimation = new BoneAnimation(animation.getName(), skeleton, animation.getSkeletonPositions().length);
            btransforms = new HashMap<Bone, ArrayList<Matrix4f>>();
            for (int i = 0; i < animation.getSkeletonPositions().length; i++) {
                buildBoneTransformMap(animation.getSkeletonPositions()[i], skeleton, i);
            }
            Set boneSet = this.btransforms.keySet();
            Iterator<Bone> biterator = boneSet.iterator();
            Bone bkey = null;
            while (biterator.hasNext()) {
                bkey = biterator.next();
                banimation.addBoneTransforms(buildBoneTransform(bkey, this.btransforms.get(bkey)));
            }
            banimation.setTimes(getTimes(animation.getFps(), animation.getSkeletonPositions().length));
            int[] interpolationTypes = new int[animation.getSkeletonPositions().length];
            for (int i = 0; i < interpolationTypes.length; i++) {
                interpolationTypes[i] = 0;
            }
            banimation.setInterpolationTypes(interpolationTypes);
            banimation.setInitialFrame(0);
            banimation.setEndFrame(animation.getSkeletonPositions().length - 1);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return banimation;
    }

    /**
     * Method that assign the transformation for a bone, creates a hashmap with
     * bones as keys and array of matrix as transformations.
     * @param skeletonPosition
     * @param skeleton : skeleton bone structure.
     * @param time : transformation time.
     */
    private void buildBoneTransformMap(SmdSkeleton skeletonPosition, Bone skeleton, int time) {
        Bone btoTransform = null;
        Bone boneParent = null;
        try {
            for (int j = 0; j < skeletonPosition.getPositions().length; j++) {
                if (j == 0) {//rootBone
                    addTransformToMap(skeletonPosition.getPositions()[0], skeleton, time);
                } else {
                    btoTransform = (Bone) skeleton.getChild(skeletonPosition.getBones()[j].getBoneName());
                    boneParent = (Bone) btoTransform.getParent();
                    Matrix4f pBindMatrix = btransforms.get(boneParent).get(time);
                    addTransformToMap(skeletonPosition.getPositions()[j], btoTransform, pBindMatrix, time);
                }
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    private void addTransformToMap(SmdBonePosition sboneTrans, Bone bone, int time) {
        try {

            ArrayList<Matrix4f> transforms = this.btransforms.get(bone);
            if (transforms == null) {
                transforms = new ArrayList<Matrix4f>();
            }
//            Matrix4f pmatrix = null;
//            if (bone.getParent() instanceof Bone) {
//                Bone parentBone = (Bone) bone.getParent();
//                pmatrix = this.btransforms.get(parentBone).get(transforms.size());
//                transforms.add(this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()).mult(pmatrix));
//            } else {//RootBone
//                transforms.add(this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
//            }
            transforms.add(time, this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
            this.btransforms.put(bone, transforms);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    private void addTransformToMap(SmdBonePosition sboneTrans, Bone bone, Matrix4f parentBindMatrix, int time) {
        try {

            ArrayList<Matrix4f> transforms = this.btransforms.get(bone);
            if (transforms == null) {
                transforms = new ArrayList<Matrix4f>();
            }
//            Matrix4f pmatrix = null;
//            if (bone.getParent() instanceof Bone) {
//                Bone parentBone = (Bone) bone.getParent();
//                pmatrix = this.btransforms.get(parentBone).get(transforms.size());
//                transforms.add(this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()).mult(pmatrix));
//            } else {//RootBone
//                transforms.add(this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
//            }
            transforms.add(time, this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
            //transforms.add(time, this.getBindMatrix(sboneTrans.getPosition(), sboneTrans.getRotation()));
            this.btransforms.put(bone, transforms);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    private BoneTransform buildBoneTransform(Bone boneToTransform, ArrayList<Matrix4f> relPositions) {
        BoneTransform bTransform = null;
        try {
            //bTransform = new BoneTransform(boneToTransform, relPositions.toArray(new Matrix4f[]{}));
            bTransform = new BoneTransform(boneToTransform, relPositions.size());
            for (int i = 0; i < relPositions.size(); i++) {
                bTransform.setRotation(i, relPositions.get(i).toRotationQuat());
                bTransform.setTranslation(i, relPositions.get(i).toTranslationVector());
            }
            bTransform.setCurrentFrame(0);
        } catch (Exception Ex) {
            Ex.printStackTrace();
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
            textFile = new File(textureURL.concat(File.separator).concat(textureName));
            TextureState ts = display.getRenderer().createTextureState();
            Texture t1 = TextureManager.loadTexture(textFile.toURI().toURL(),
                    Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear);
            ts.setTexture(t1, 0);
            mesh.setRenderState(ts);
        } catch (Exception Ex) {
            Ex.printStackTrace();
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
        Bone[] bones = null;
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
                ArrayList<SmdVertex> bv = sbones[i].getVertexInfluence();
                if (bv != null) {
                    for (int j = 0; j < bv.size(); j++) {
                        this.addBoneInfluence(bv.get(j).getGeomIndex(), bv.get(j).getVertIndex(), bones[i], 1f);
                    }
                }
            }
            rootBone = bones[0];
            rootBone.updateGeometricState(0, true);
            this.setSkeleton(rootBone);
            this.attachChild(rootBone);
            this.normalizeWeights();
            this.regenInfluenceOffsets();

        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }

    private Matrix4f getBindMatrix(Vector3f tras, Vector3f rot) {
        Matrix4f bmatrix = null;
        Quaternion q = null;
        try {
            bmatrix = new Matrix4f();
            bmatrix.setIdentity();
            bmatrix.setTranslation(tras);
            //bmatrix.setRotationQuaternion(new Quaternion(rot.toArray(null)));
            q = buildQuat(rot);
            bmatrix.setRotationQuaternion(q);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return bmatrix;
    }

    private Quaternion buildQuat(Vector3f rot) {
        Quaternion q = null;
        try {
            q = new Quaternion();
            q.fromRotationMatrix(this.buildMatrix(rot));
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return q;
    }

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
            Ex.printStackTrace();
        }
        return m3.mult(m2).mult(m1);
    }



    public static void main(String[] args) {
        try {
            SmdNode node = new SmdNode("C:\\JME\\Models\\player\\Molly\\Molly.qc", null);
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
}
