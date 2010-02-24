package com.base.model.smd.test;

import com.base.model.jme.SMDNode;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.app.SimpleGame;
import com.jme.scene.shape.AxisRods;
import com.jme.util.BoneDebugger;

/**
 *
 * @author serser
 */
public class TestSMD extends SimpleGame {

    private SMDNode node;
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
        //node = new SMDNode("C:\\JME\\Models\\Zombie\\Zombie.qc", display);
        //VAMPIRE SLAYER
        node = new SMDNode("C:\\JME\\Models\\player\\eightball\\eightball.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\Molly\\Molly.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\nina\\nina.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\Hands\\Winchester\\v_winchester.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\edgar\\edgar.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\fatherd\\fatherd.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\louis\\louis.qc", display);
        //BRAIN
        //node = new SMDNode("C:\\JME\\Models\\Zombie1\\zombie1.qc", display);
        //THE TRENCHES
        //node = new SMDNode("C:\\JME\\Models\\player\\british-infantry\\british-infantry.qc", display);
        //UNDERWORLD
        //node = new SMDNode("C:\\JME\\Models\\player\\lycan_rebel\\lycan_rebel.qc", display);
        //DAY OF DEFEAT
        //node = new SMDNode("C:\\JME\\Models\\player\\axis-inf\\axis-inf.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\axis-para\\axis-para.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\brit-inf\\brit-inf.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\us-inf\\us-inf.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\us-para\\us-para.qc", display);
        //COUNTER STRIKE
        //node = new SMDNode("C:\\JME\\Models\\player\\gign\\gign.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\arctic\\arctic.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\gsg9\\gsg9.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\guerilla\\guerilla.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\leet\\leet.qc", display);
        node = new SMDNode("C:\\JME\\Models\\player\\terror\\terror.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\urban\\urban.qc", display);
        //NATURAL SELECTION
        //node = new SMDNode("C:\\JME\\Models\\player\\alien1\\alien1.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\alien2\\alien2.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\alien3\\alien3.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\alien4\\alien4.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\player\\alien5\\alien5.qc", display);
        
        /*****  ITEMS  *****/
//        node = new SMDNode("C:\\JME\\Models\\Cars\\dod_jagd_car\\dod_jagd_car.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\DodTree\\dod_tree1.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\bottle1\\bottle1.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\bottle2\\bottle2.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\chest\\chest.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\casket\\casket01.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\statue\\by_statue_snow.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\xen_tree4_1\\xen_tree4_1.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\vsghost\\vsghost.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\vsghost\\vsghost.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\pissoir\\pissoir.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\klo\\klo.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\xen_van3_2\\xen_van3_2.qc", display);
//        node = new SMDNode("C:\\JME\\Models\\xen_rustcar\\xen_rustcar.qc", display);
        //node = new SMDNode("C:\\JME\\Models\\xen_car4_2\\xen_car4_2.qc", display);
        node.getController().setActiveAnimation("run");
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
