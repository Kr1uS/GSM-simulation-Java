import java.io.*;
import java.util.ArrayList;

public class FileManager {

    private ArrayList<String[]> info;
    private Encoder encoder;

    public FileManager() {
        this.info = new ArrayList<>();
        encoder = new Encoder();
    }

    public void addInfo(String PDU) {
        SMS sms = new SMS(PDU);
        String[] message = new String[3];
        message[0] = sms.getSender();
        message[1] = sms.getMessage();
        message[2] = sms.getRecipient();
        info.add(message);
    }


    public void writeToFile() {
        try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("info.bin")))) {
            for (String[] message : info) {
                outputStream.writeUTF(message[0]);
                outputStream.writeUTF(message[1]);
                outputStream.writeUTF(message[2]);
            }
            System.out.println("Data has been written to the file successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while writing data to the file.");
            e.printStackTrace();
        }
    }

    public void readFromFile() {
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("info.bin")))) {
            while (inputStream.available() > 0) {
                String sender = inputStream.readUTF();
                String message = inputStream.readUTF();
                String recipient = inputStream.readUTF();
                System.out.printf("%s %s %s\n",
                        sender, message, recipient);
            }
            System.out.println("Data has been read from the file successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while reading data from the file.");
            e.printStackTrace();
        }
    }

}
