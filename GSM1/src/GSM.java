import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class GSM implements Listener {
    private ArrayList<VBD> vbdArrayList;
    private ArrayList<VRD> vrdArrayList;
    private PriorityBlockingQueue<BTS> BTSFirstLayer;
    private ArrayList<PriorityBlockingQueue<BSC>> BSCLayers;

    private PriorityBlockingQueue<BTS> BTSSecondLayer;
    private GraphicListener graphicListener;
    private FileManager fileManager;

    public GSM() {
        this.vbdArrayList = new ArrayList<>();
        this.vrdArrayList = new ArrayList<>();
        this.BTSFirstLayer = new PriorityBlockingQueue<>();
        this.BSCLayers = new ArrayList<>();
        this.BTSSecondLayer = new PriorityBlockingQueue<>();
        this.fileManager = new FileManager();
    }

    public void initialise(GraphicListener graphicListener) {
        this.graphicListener = graphicListener;

        Event event = new Event(this);

        event.setValueInt(0);
        BTS bts1 = new BTS(this);
        BTSFirstLayer.add(bts1);
        bts1.start();
        graphicListener.createBTS(event);

        event.setValueInt(1);
        BTS bts2 = new BTS(this);
        BTSSecondLayer.add(bts2);
        bts2.start();
        graphicListener.createBTS(event);

        event.setValueInt(0);
        PriorityBlockingQueue<BSC> bscLayer = new PriorityBlockingQueue<>();
        BSC bsc = new BSC(this);
        bscLayer.add(bsc);
        BSCLayers.add(bscLayer);
        bsc.start();
        graphicListener.createBSC(event);
    }


    @Override
    public ArrayList<VBD> vbdArrayList() {
        return vbdArrayList;
    }

    @Override
    public VBD createVBD(Event event) {
        VBD vbd = new VBD(this);
        vbd.setMessage(event.getValueString());
        vbdArrayList.add(vbd);
        vbd.start();
        return vbd;
    }

    @Override
    public void setFrequencyVBD(Event event, VBD vbd) {
        vbd.setFrequency(event.getValueInt());
    }

    @Override
    public void suspendVBD(Event event, VBD vbd) { vbd.Suspend(); }

    @Override
    public void notifyVBD(Event event, VBD vbd) { vbd.Notify(); }

    @Override
    public void terminateVBD(Event event, VBD vbd) { vbd.Terminate(); }

    @Override
    public ArrayList<VRD> vrdArrayList() {
        return vrdArrayList;
    }

    @Override
    public VRD createVRD(Event event) {
        VRD vrd = new VRD(this);
        vrdArrayList.add(vrd);
        vrd.start();
        return vrd;
    }

    @Override
    public void setCleaningVRD(Event event, VRD vrd) {
        vrd.setCleaning(event.getValueBoolean());
    }

    @Override
    public void terminateVRD(Event event, VRD vrd) {
        vrd.Terminate();
    }

    @Override
    public PriorityBlockingQueue<BTS> BTSFirstLayer() {
        return BTSFirstLayer;
    }

    @Override
    public ArrayList<PriorityBlockingQueue<BSC>> BSCLayers() {
        return this.BSCLayers;
    }

    @Override
    public PriorityBlockingQueue<BTS> BTSSecondLayer() {
        return BTSSecondLayer;
    }

    @Override
    public PriorityBlockingQueue<BSC> createBSCLayer() {
        PriorityBlockingQueue<BSC> bscLayer = new PriorityBlockingQueue<>();
        BSC bsc = new BSC(this);
        bscLayer.add(bsc);
        bsc.start();
        Event event = new Event(this);
        event.setValueInt(0);
        graphicListener.createBSC(event);
        this.BSCLayers.add(bscLayer);
        return bscLayer;
    }

    @Override
    public BSC createBSC(BSC bsc) {
        for (PriorityBlockingQueue<BSC> bscLayer : BSCLayers) {
            if (bscLayer.contains(bsc)) {
                BSC bscNew = new BSC(this);
                bscLayer.add(bscNew);
                return bscNew;
            }
        } return null;
    }

    @Override
    public BTS createBTS(BTS bts) {
        BTS btsNew = new BTS(this);
        if (BTSFirstLayer.contains(bts))
            BTSFirstLayer.add(btsNew);
        else if (BTSSecondLayer.contains(bts))
            BTSSecondLayer.add(btsNew);
        return btsNew;
    }


    @Override
    public void closing() {
        fileManager.writeToFile();
        fileManager.readFromFile();
        System.exit(0);
    }

    @Override
    public FileManager fileManager() {
        return fileManager;
    }

    @Override
    public void terminateBTS(Event event, BTS bts) {
        if (BTSFirstLayer.contains(bts))
            BTSFirstLayer.remove(bts);
        else if (BTSSecondLayer.contains(bts))
            BTSSecondLayer.remove(bts);
    }

    @Override
    public void terminateBSC(Event event, BSC bsc) {
        for (PriorityBlockingQueue<BSC> bscLayer: BSCLayers) {
            if (bscLayer.contains(bsc))
                bscLayer.remove(bsc);
        }
    }

    @Override
    public void terminateBSCLayer(Event event) {
        BSCLayers.remove(BSCLayers.size()-1);
    }
}

//--SMS--//
class SMS {
    private String message;
    private String sender;
    private String recipient;
    private String PDU;
    private Encoder encoder;

    public SMS(String pdu) {
        encoder = new Encoder();
        String decodedPDU = encoder.decode(pdu);
        if (decodedPDU != null) {
            String[] parts = decodedPDU.split("\n");
            if (parts.length == 3) {
                sender = parts[0];
                message = parts[1];
                recipient = parts[2];
            }
        }
        this.PDU =pdu;
    }


    public SMS(String message, String sender, String recipient) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        encoder = new Encoder();
        PDU = encoder.encode(this);
    }


    public String getPDU() {
        return PDU;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }



}

//--DEVICE--//
abstract class Device extends Thread {
    String number;
    Listener listener;

    public Device(Listener listener) {
        number = generateNumber();
        this.listener = listener;
    }


    private String generateNumber() {
        String number = "+" + (int) (Math.random() * 900) + 100;
        return number;
    }

}

//--VBD--//
class VBD extends Device {
    private int frequency;
    private boolean suspended;
    private boolean terminated;
    private String message;
    public VBD(Listener listener) {
        super(listener);
        suspended = false;
        terminated = false;
        frequency = 5;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        while(!terminated) {
            if (!existVRD())
                continue;
            if(suspended)
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            else {
                setFrequency(frequency);
                try {
                    sendSMS();
                    this.sleep(frequency * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean existVRD() {
        return !(super.listener.vrdArrayList().size() == 0);
    }

    public void sendSMS() {
        VRD vrd = getRandomVRD();
        SMS sms = new SMS(message, number, vrd.number);
        System.out.printf("Message '%s' was sent from %s to %s\n",
                sms.getMessage(), sms.getSender(), sms.getRecipient());
        listener.BTSFirstLayer().peek().receiveSMS(sms.getPDU());
        super.listener.fileManager().addInfo(sms.getPDU());
    }

    public VRD getRandomVRD() {
        int maxID = super.listener.vrdArrayList().size();
        int random = (int) (Math.random()*maxID);
        return super.listener.vrdArrayList().get(random);
    }

    public void Notify(){
        System.out.println("VBD " + super.number + " was notified.");
        suspended = false;
        synchronized (this) {
            this.notify();
        }
    }
    public void Suspend() {
        suspended = true;
        System.out.println("VBD " + super.number + " was suspended.");
    }

    public void Terminate() {
        terminated = true;
        System.out.println("VBD " + super.number + " was terminated.");
    }


    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}

//--VRD--//
class VRD extends Device {
    private int receivedSMS;
    private boolean cleaning;
    private boolean terminated;
    public VRD(Listener listener) {
        super(listener);
        cleaning = false;
        terminated = false;
    }

    public void setCleaning(boolean cleaning) {
        this.cleaning = cleaning;
    }

    public void receiveSMS(String PDU) {
        receivedSMS += 1;
        SMS sms = new SMS(PDU);
        System.out.printf("Message '%s' was received from %s to %s\n",
                sms.getMessage(), sms.getSender(), sms.getRecipient());
    }

    public int getReceivedSMS(){
        return receivedSMS;
    }

    @Override
    public void run() {
        while(!terminated) {
            try {
//                System.out.println(receivedSMS);
                this.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void Terminate() {
        terminated = true;
        System.out.println("VBD " + super.number + " was terminated.");
    }

}

//--STATIONS--//
abstract class Station extends Thread {
    String number;
    Listener listener;

    public Station(Listener listener) {
        number = generateNumber();
        this.listener = listener;
    }

    public String getNumber() {
        return number;
    }

    private String generateNumber() {
        String number = "#" + (int) (Math.random() * 900) + 100;
        return number;
    }

    public int generateInt() {
        int i = (int) (Math.random()*10 + 5);
        return i;
    }

}
class BTS extends Station implements Comparable<BTS> {
    private ArrayList<SMS> smsArrayList;
    private boolean terminated;
    private int limit;

    public BTS(Listener listener) {
        super(listener);
        smsArrayList = new ArrayList<>();
        terminated = false;
        limit = 5;
    }

    @Override
    public void run() {
        while (!terminated) {
            try {
                this.sleep(5000);
                for (SMS sms : smsArrayList)
                    passNext(sms);
                smsArrayList.clear();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public synchronized void receiveSMS(String PDU) {
        if (smsArrayList.size() < limit) {
            smsArrayList.add(new SMS(PDU));
            System.out.printf("Message was received by %s\n", number);
        } else {
            BTS bts = super.listener.createBTS(this);
            bts.receiveSMS(PDU);
        }
    }

    public int getNumberOfSMS() {
        return smsArrayList.size();
    }

    public synchronized void passNext(SMS sms) {
        if (super.listener.BTSFirstLayer().contains(this))
            synchronized (super.listener.BSCLayers().get(0).peek()) {
                super.listener.BSCLayers().get(0).peek().receiveSMS(sms.getPDU());
            }
        else if (super.listener.BTSSecondLayer().contains(this))
            for (VRD vrd : super.listener.vrdArrayList())
                if (vrd.number.equals(sms.getRecipient()))
                    synchronized (vrd) {
                        vrd.receiveSMS(sms.getPDU());
                    }
    }

    @Override
    public int compareTo(BTS o) {
        return o.smsArrayList.size() - this.smsArrayList.size();
    }
}

class BSC extends Station implements Comparable<BSC>{
    private ArrayList<SMS> smsArrayList;
    private boolean terminated;
    private int limit;

    public BSC(Listener listener) {
        super(listener);
        smsArrayList = new ArrayList<>();
        terminated = false;
        limit = 5;
    }

    @Override
    public void run() {
        while (!terminated) {
            try {
                this.sleep(super.generateInt()*1000);
                for (SMS sms : smsArrayList)
                    passNext(sms);
                smsArrayList.clear();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public synchronized void receiveSMS(String PDU) {
        smsArrayList.add(new SMS(PDU));
        System.out.println(PDU);
        System.out.printf("Message was received by %s\n", number);
    }


    public synchronized void passNext(SMS sms) {
        for (PriorityBlockingQueue<BSC> bscLayer: super.listener.BSCLayers())
            if (bscLayer.contains(this))
                if (super.listener.BSCLayers().indexOf(bscLayer) == super.listener.BSCLayers().size()-1)
                    synchronized (super.listener.BTSSecondLayer().peek()) {
                        super.listener.BTSSecondLayer().peek().receiveSMS(sms.getPDU());
                    }
                else
                    synchronized (super.listener.BSCLayers().get(
                            super.listener.BSCLayers().indexOf(bscLayer)+1
                                  ).peek()) {
                        super.listener.BSCLayers().get(
                                super.listener.BSCLayers().indexOf(bscLayer)+1
                        ).peek().receiveSMS(sms.getPDU());
                    }
    }

    @Override
    public int compareTo(BSC o) {
        return o.smsArrayList.size() - this.smsArrayList.size();
    }
}