import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

public interface Listener {

    ArrayList<VBD> vbdArrayList();
    VBD createVBD(Event event);
    void setFrequencyVBD(Event event, VBD vbd);
    void suspendVBD(Event event, VBD vbd);
    void notifyVBD(Event event, VBD vbd);
    void terminateVBD(Event event, VBD vbd);

    ArrayList<VRD> vrdArrayList();
    VRD createVRD(Event event);
    void setCleaningVRD(Event event, VRD vrd);
    void terminateVRD(Event event, VRD vrd);

    PriorityBlockingQueue<BTS> BTSFirstLayer();
    ArrayList<PriorityBlockingQueue<BSC>> BSCLayers();
    PriorityBlockingQueue<BTS> BTSSecondLayer();

    PriorityBlockingQueue<BSC> createBSCLayer();
    BSC createBSC(BSC bsc);
    BTS createBTS(BTS bts);

    void closing();
    FileManager fileManager();

    void terminateBTS(Event event, BTS bts);

    void terminateBSC(Event event, BSC bsc);

    void terminateBSCLayer(Event event);
}

