import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Graphics extends JFrame implements GraphicListener {

    private Listener listener;

    public Graphics() {

    }

    public void initialise(Listener listener) {
        this.listener = listener;
        setPanels();
    }

    private void setPanels() {
        DevicesPanel vbdsPanel = new DevicesPanel(true, listener, this);
        this.getContentPane().add( vbdsPanel, BorderLayout.LINE_START );

        DevicesPanel vrdsPanel = new DevicesPanel(false, listener, this);
        this.getContentPane().add( vrdsPanel, BorderLayout.LINE_END );

        StationsPanel stationsPanel = new StationsPanel(listener, this);
        this.getContentPane().add( stationsPanel, BorderLayout.CENTER );
    }

    public void openWindow(int x, int y) {
        this.setSize(x, y);
        this.setBackground(Color.gray);
        this.setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listener.closing();
                dispose();
            }
        });
    }

    @Override
    public void createBTS(Event event) {
//        System.out.println("Graphics for BTS been created");
//        StationsPanel
    }

    @Override
    public void createBSC(Event event) {
//        System.out.println("Graphics for BSC been created");
    }

    @Override
    public int getReceivedSMS(VRD vrd) {
        return vrd.getReceivedSMS();
    }

}

//--MAIN-PANELS--//
class DevicesPanel extends JPanel {
    private JScrollPane scrollPane;
    private JPanel devicesPanel;
    private JButton addButton;
    private boolean isVBD;
    private Listener listener;
    private GraphicListener graphicListener;

    public DevicesPanel(boolean isVBD, Listener listener, GraphicListener graphicListener) {
        this.isVBD = isVBD;
        this.listener = listener;
        this.graphicListener = graphicListener;

        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        this.setPreferredSize(new Dimension( 280, 1080));

        devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(devicesPanel);
        add(scrollPane, BorderLayout.CENTER);

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isVBD)
                    createVBD();
                else
                    createVRD();
            }
        });
        add(addButton, BorderLayout.SOUTH);
    }

    public void createVBD() {
        String message = JOptionPane.showInputDialog("Enter a text message:");
        Event event = new Event(this);
        event.setValueString(message);
        VBDPanel vbdPanel = new VBDPanel(listener.createVBD(event), listener, graphicListener);
        devicesPanel.add(vbdPanel);
        revalidate();
        repaint();
    }

    public void createVRD() {
        int index = devicesPanel.getComponentCount();
        Event event = new Event(this);
        VRDPanel vrdPanel = new VRDPanel(listener.createVRD(event), listener, graphicListener);
        devicesPanel.add(vrdPanel);
        revalidate();
        repaint();
    }

}

class StationsPanel extends JPanel {
    private Listener listener;
    private GraphicListener graphicListener;
    public StationsPanel(Listener listener, GraphicListener graphicListener) {
        this.listener = listener;
        this.graphicListener = graphicListener;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        this.setPreferredSize(new Dimension( 1300, 1080));
        setPanels();
    }

    private void setPanels() {
        BTSsPanel btSsPanel1 = new BTSsPanel(listener, graphicListener);
        this.add(btSsPanel1, BorderLayout.LINE_START);

        BTSsPanel btSsPanel2 = new BTSsPanel(listener, graphicListener);
        this.add(btSsPanel2, BorderLayout.LINE_END);

        BSCsPanel bsCsPanel = new BSCsPanel(listener, graphicListener);
        this.add(bsCsPanel, BorderLayout.CENTER);
    }

}

//--SUB-PANELS--//
class BTSsPanel extends JPanel implements GraphicListener{

    private JScrollPane scrollPane;
    private JPanel btssPanel;
    private Listener listener;
    private GraphicListener graphicListener;

    public BTSsPanel(Listener listener, GraphicListener graphicListener) {
        this.graphicListener = graphicListener;
        this.listener = listener;
        btssPanel = new JPanel();
        btssPanel.setLayout(new BoxLayout(btssPanel, BoxLayout.Y_AXIS));
        btssPanel.setPreferredSize(new Dimension(80, 1080));

        scrollPane = new JScrollPane(btssPanel);
        add(scrollPane, BorderLayout.CENTER);

    }

    @Override
    public void createBTS(Event event) {
        BTS bts = new BTS(listener);
        BTSPanel btsPanel = new BTSPanel(bts, listener, graphicListener);
        btssPanel.add(btsPanel);
        revalidate();
        repaint();
    }

    @Override
    public void createBSC(Event event) {

    }

    @Override
    public int getReceivedSMS(VRD vrd) {
        return 0;
    }
}

class BSCsPanel extends JPanel implements GraphicListener{
    private JScrollPane scrollPane;
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JPanel bscsPanel;
    private Listener listener;
    private GraphicListener graphicListener;

    public BSCsPanel(Listener listener, GraphicListener graphicListener) {
        setLayout(new BorderLayout());
        this.listener = listener;
        this.graphicListener = graphicListener;
        bscsPanel = new JPanel();
        bscsPanel.setLayout(new BoxLayout(bscsPanel, BoxLayout.Y_AXIS));
        bscsPanel.setPreferredSize(new Dimension(1140, 1080));

        scrollPane = new JScrollPane(bscsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        buttonAdd = new JButton("+");
        buttonAdd.setPreferredSize(new Dimension(300, 40));
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBSCLayer();
            }
        });
        buttonsPanel.add(buttonAdd, BorderLayout.LINE_START);
        buttonRemove = new JButton("-");
        buttonRemove.setPreferredSize(new Dimension(300, 40));
        buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonsPanel.add(buttonRemove, BorderLayout.LINE_END);

        add(buttonsPanel, BorderLayout.SOUTH);

    }

    private void createBSCLayer(){
        listener.createBSCLayer();
        scrollPane = new JScrollPane(bscsPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void removeBSCLayer() {
        listener.terminateBSCLayer(new Event(this));
    }

    @Override
    public void createBTS(Event event) {

    }

    @Override
    public void createBSC(Event event) {
        BSC bsc = new BSC(listener);
        BSCPanel bscPanel = new BSCPanel(bsc, listener, graphicListener);
        bscsPanel.add(bscPanel);
        revalidate();
        repaint();
    }

    @Override
    public int getReceivedSMS(VRD vrd) {
        return 0;
    }
}


//--ELEMENT-PANELS--//
class VBDPanel extends JPanel {
    private VBD vbd;
    private JSlider frequencySlider;
    private JButton terminateButton;
    private JTextField deviceNumberField;
    private JComboBox<String> stateComboBox;
    private Listener listener;
    private GraphicListener graphicListener;
    public VBDPanel(VBD vbd, Listener listener, GraphicListener graphicListener) {
        this.vbd = vbd;
        this.listener = listener;
        this.graphicListener = graphicListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(80, getPreferredSize().height));

        JPanel vbdNumberPanel = new JPanel();
        vbdNumberPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        String vbdNumber = "Device Number:\n" + vbd.number;
        JLabel vbdNumberLabel = new JLabel(vbdNumber);
        vbdNumberPanel.add(vbdNumberLabel);

        JPanel frequencyPanel = new JPanel();
        frequencyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel frequencyLabel = new JLabel("Frequency:");
        frequencySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        frequencySlider.setMajorTickSpacing(1);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setPaintLabels(true);
        frequencySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeFrequency(frequencySlider.getValue());
            }
        });
        frequencyPanel.add(frequencyLabel);
        frequencyPanel.add(frequencySlider);

        JPanel statePanel = new JPanel();
        statePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel stateLabel = new JLabel("State:");
        String[] states = {"ACTIVE", "WAITING"};
        stateComboBox = new JComboBox<>(states);
        stateComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVBDState((String) stateComboBox.getSelectedItem());
            }
        });
        statePanel.add(stateLabel);
        statePanel.add(stateComboBox);

        JPanel terminatePanel = new JPanel();
        terminatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel terminateLabel = new JLabel("Terminate:");
        terminateButton = new JButton("X");
        terminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVBDState("TERMINATE");
            }
        });
        terminatePanel.add(terminateLabel);
        terminatePanel.add(terminateButton);

        add(vbdNumberPanel);
        add(frequencyPanel);
        add(statePanel);
        add(terminatePanel);
    }


    private void changeFrequency(int value) {
        Event event = new Event(this);
        event.setValueInt(value);
        listener.setFrequencyVBD(event, vbd);
    }

    private void updateVBDState(String state) {
        Event event = new Event(this);
        switch (state) {
            case ("ACTIVE") -> listener.notifyVBD(event, vbd);
            case ("WAITING") -> listener.suspendVBD(event, vbd);
            case ("TERMINATE") -> {
                listener.terminateVBD(event, vbd);
                Container parent = getParent();
                if (parent instanceof JPanel) {
                    JPanel devicesPanel = (JPanel) parent;
                    devicesPanel.remove(this);
                    devicesPanel.revalidate();
                    devicesPanel.repaint();
                }
            }
        }
    }

}

class VRDPanel extends JPanel {
    private VRD vrd;
    private JButton terminateButton;
    private JLabel receivedMessagesLabel;
    private JCheckBox cleaningCheckBox;
    private Listener listener;
    private GraphicListener graphicListener;

    public VRDPanel(VRD vrd, Listener listener, GraphicListener graphicListener) {
        this.vrd = vrd;
        this.listener = listener;
        this.graphicListener = graphicListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(80, getPreferredSize().height));

        JPanel vrdNumberPanel = new JPanel();
        vrdNumberPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        String vrdNumber = "Device Number:\n" + vrd.number;
        JLabel vrdNumberLabel = new JLabel(vrdNumber);
        vrdNumberPanel.add(vrdNumberLabel);

        JPanel receivedMessagesPanel = new JPanel();
        receivedMessagesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel receivedMessagesTitleLabel = new JLabel("Received Messages:");
        receivedMessagesLabel = new JLabel(String.valueOf(graphicListener.getReceivedSMS(vrd)));
        receivedMessagesPanel.add(receivedMessagesTitleLabel);
        receivedMessagesPanel.add(receivedMessagesLabel);

        JPanel cleaningPanel = new JPanel();
        cleaningPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel cleaningLabel = new JLabel("Clear messages:");
        cleaningCheckBox = new JCheckBox();
        cleaningCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCleaning(cleaningCheckBox.isSelected());
            }
        });
        cleaningPanel.add(cleaningLabel);
        cleaningPanel.add(cleaningCheckBox);

        JPanel terminatePanel = new JPanel();
        terminatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel terminateLabel = new JLabel("Terminate:");
        terminateButton = new JButton("X");
        terminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminateVRD();
            }
        });
        terminatePanel.add(terminateLabel);
        terminatePanel.add(terminateButton);

        add(vrdNumberPanel);
        add(receivedMessagesPanel);
        add(cleaningPanel);
        add(terminatePanel);
    }

    private void updateCleaning(boolean value) {
        Event event = new Event(this);
        listener.setCleaningVRD(event, vrd);
    }

    private void terminateVRD() {
        Event event = new Event(this);
        listener.terminateVRD(event, vrd);
        Container parent = getParent();
        if (parent instanceof JPanel) {
            JPanel devicesPanel = (JPanel) parent;
            devicesPanel.remove(this);
            devicesPanel.revalidate();
            devicesPanel.repaint();
        }
    }


}

class BTSPanel extends JPanel {
    private BTS bts;
    private JButton terminateButton;
    private JLabel numberLabel;
    private Listener listener;
    private GraphicListener graphicListener;

    public BTSPanel(BTS bts, Listener listener, GraphicListener graphicListener) {
        this.bts = bts;
        this.listener = listener;
        this.graphicListener = graphicListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(80, getPreferredSize().height));

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        String btsNumber = "BTS Number:\n" + bts.getNumber();
        numberLabel = new JLabel(btsNumber);
        numberPanel.add(numberLabel);

        JPanel terminatePanel = new JPanel();
        terminatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel terminateLabel = new JLabel("Terminate:");
        terminateButton = new JButton("X");
        terminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminateBTS();
            }
        });
        terminatePanel.add(terminateLabel);
        terminatePanel.add(terminateButton);

        add(numberPanel);
        add(terminatePanel);
    }

    private void terminateBTS() {
        Event event = new Event(this);
        listener.terminateBTS(event, bts);
        Container parent = getParent();
        if (parent instanceof JPanel) {
            JPanel btssPanel = (JPanel) parent;
            btssPanel.remove(this);
            btssPanel.revalidate();
            btssPanel.repaint();
        }
    }
}

class BSCPanel extends JPanel {
    private BSC bsc;
    private JButton terminateButton;
    private JLabel numberLabel;
    private Listener listener;
    private GraphicListener graphicListener;

    public BSCPanel(BSC bsc, Listener listener, GraphicListener graphicListener) {
        this.bsc = bsc;
        this.listener = listener;
        this.graphicListener = graphicListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(80, getPreferredSize().height));

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        String bscNumber = "BSC Number:\n" + bsc.getNumber();
        numberLabel = new JLabel(bscNumber);
        numberPanel.add(numberLabel);

        JPanel terminatePanel = new JPanel();
        terminatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel terminateLabel = new JLabel("Terminate:");
        terminateButton = new JButton("X");
        terminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminateBSC();
            }
        });
        terminatePanel.add(terminateLabel);
        terminatePanel.add(terminateButton);

        add(numberPanel);
        add(terminatePanel);
    }

    private void terminateBSC() {
        Event event = new Event(this);
        listener.terminateBSC(event, bsc);
        Container parent = getParent();
        if (parent instanceof JPanel) {
            JPanel bcssPanel = (JPanel) parent;
            bcssPanel.remove(this);
            bcssPanel.revalidate();
            bcssPanel.repaint();
        }
    }
}