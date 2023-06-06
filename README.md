# GSM-simulation-Java
Multithreaded application which simulates the work of the Global System for Mobile Communications using Java and Swing.

# Application operation
The user of the application creates a certain number of VBD objects (sending devices), each of which is a separate thread and immediately starts transmitting the message entered during creation. As according to the standard, each SMS has the encoded sender and recipient numbers along with the message, a random VRD element is chosen as the recipient. The created SMS is passed to the BTS station with the least number of waiting SMSs.The application in its running state has three layers. There is always at least one intermediate layer of BSC controllers between the input layer and the output layer. The number of intermediate layers depends on the user’s actions in the application, who can add or remove a layer using the keys. Each newly created communication layer will be created with one BSC, while removing a layer will result in no longer accepting messages by that layer and immediately passing messages from all BSCs, skipping the transmission timers.
#
• each BSC stores the SMS for a random timer (from 5 to 15 sec) and then passes it on to the next layer; #
• each BTS passes the SMS to the next layer or VRD after 3 seconds; #
• the BTS/BSC with the least number of SMSs is always selected when passing theSMS to the next layer; #
• the messages are transmitted in the form of PDU including encoded sender, recipient and the message itself. #
• when the application is closed, a binary file is created representing information about each VBD and the message that was sent. #
#
