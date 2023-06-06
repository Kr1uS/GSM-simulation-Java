import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {

        GSM gsm = new GSM();

        Graphics graphics = new Graphics();

        graphics.initialise(gsm);
        gsm.initialise(graphics);

        graphics.openWindow(1920, 1080);

    }

}