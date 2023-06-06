public class Encoder {
    public Encoder() {
    }

    public String encode(SMS sms) {
        String PDU = "";
        String message = sms.getMessage();
        String sender = sms.getSender();
        String recipient = sms.getRecipient();
        PDU += encodeUserData(sender) + '\n'
                + encodeUserData(message) + '\n'
                + encodeUserData(recipient);
        return PDU;
    }

    public String decode(String pdu) {
        if (pdu != null) {
            String[] parts = pdu.split("\n");
            if (parts.length == 3) {
                String sender = decodeUserData(parts[0]);
                String message = decodeUserData(parts[1]);
                String recipient = decodeUserData(parts[2]);
                return sender + '\n' + message + '\n' + recipient;
            }
        }
        return null;
    }


    private String encodeUserData(String userData) {
        StringBuilder encodedDataBuilder = new StringBuilder();
        for (int i = 0; i < userData.length(); i++) {
            char currentChar = userData.charAt(i);
            int currentCharIndex = (int) currentChar;
            String encodedChar = Integer.toHexString(currentCharIndex);
            encodedDataBuilder.append(encodedChar);
        }
        return encodedDataBuilder.toString();
    }

    private String decodeUserData(String pdu) {
        StringBuilder decodedDataBuilder = new StringBuilder();
        for (int i = 0; i < pdu.length(); i += 2) {
            String encodedChar = pdu.substring(i, i + 2);
            int charCode = Integer.parseInt(encodedChar, 16);
            char decodedChar = (char) charCode;
            decodedDataBuilder.append(decodedChar);
        }
        return decodedDataBuilder.toString();
    }
}
