package com.base.model.smd.test;

import com.base.model.jme.SmdNode;
import com.jme.app.AbstractGame.ConfigShowMode;

import com.jme.app.SimpleGame;
import com.jme.input.ChaseCamera;
import com.jme.math.Vector3f;
import com.jme.scene.shape.AxisRods;
import com.jme.util.BoneDebugger;

/**
 *
 * @author serser
 */
public class TestSMD extends SimpleGame {

    private SmdNode node;
    AxisRods ejes;

    public TestSMD() {
    }

    /**
     * Entry point for the test,
     *
     * @param args
     */
    public static void main(String[] args) {
        TestSMD app = new TestSMD();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * builds the trimesh.
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        ejes = new AxisRods("ejes", true, 5.0f);
        ejes.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(ejes);
        /*****  PLAYERS  *****/
        //node = new SmdNode("C:\\JME\\Models\\Zombie\\Zombie.qc", display);
        node = new SmdNode("C:\\JME\\Models\\player\\eightball\\eightball.qc", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\Molly\\Molly.qc", "C:\\JME\\Models\\player\\Molly\\Molly.smd","C:\\JME\\Models\\player\\Molly\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\nina\\nina.qc", "C:\\JME\\Models\\player\\nina\\nina_new.smd","C:\\JME\\Models\\player\\nina\\Texture.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\Hands\\Winchester\\v_winchester.qc", "C:\\JME\\Models\\Hands\\Winchester\\reference.smd","C:\\JME\\Models\\Hands\\Winchester\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\edgar\\edgar.qc", "C:\\JME\\Models\\player\\edgar\\gent.smd","C:\\JME\\Models\\player\\edgar\\dm_face.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\fatherd\\fatherd.qc", "C:\\JME\\Models\\player\\fatherd\\fd.smd","C:\\JME\\Models\\player\\fatherd\\dm_face.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\louis\\louis.qc", "C:\\JME\\Models\\player\\louis\\louis.smd","C:\\JME\\Models\\player\\louis\\dm_face.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\Zombie1\\zombie1.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\british-infantry\\british-infantry.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\lycan_rebel\\lycan_rebel.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\arctic\\arctic.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\axis-inf\\axis-inf.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\axis-para\\axis-para.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\brit-inf\\brit-inf.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\gign\\gign.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\gsg9\\gsg9.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\guerilla\\guerilla.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\leet\\leet.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\terror\\terror.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\urban\\urban.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\us-inf\\us-inf.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\us-para\\us-para.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\civleader\\civLeader.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\alien1\\alien1.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\alien2\\alien2.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\alien3\\alien3.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\alien4\\alien4.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\player\\alien5\\alien5.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        /*****  ITEMS  *****/
        //node = new SmdNode("C:\\JME\\Models\\Cars\\dod_jagd_car\\dod_jagd_car.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\DodTree\\dod_tree1.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\bottle1\\bottle1.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\bottle2\\bottle2.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\chest\\chest.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\casket\\casket01.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\statue\\by_statue_snow.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\xen_tree4_1\\xen_tree4_1.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\vsghost\\vsghost.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\vsghost\\vsghost.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\pissoir\\pissoir.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\klo\\klo.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\xen_van3_2\\xen_van3_2.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\xen_rustcar\\xen_rustcar.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        //node = new SmdNode("C:\\JME\\Models\\xen_car4_2\\xen_car4_2.qc", "C:\\JME\\Models\\player\\eightball\\eightball.smd","C:\\JME\\Models\\player\\eightball\\null.bmp", display);
        rootNode.attachChild(node);

    }

    @Override
    protected void simpleRender() {
        //super.simpleRender();
        BoneDebugger.drawBones(rootNode, display.getRenderer(), true);      
    }

    @Override
    protected void updateInput() {
        super.updateInput();
    }

    @Override
    public void simpleUpdate() {
        super.simpleUpdate();
        timer.update();
    }
}
