The finished project aims to connect users of the program in a chat system. This project aims to acheive;
  * The connecting and contact discovery phase
  * The messaging: Peer2peer messaging
  * The implementation of a chat system interface
  * Handling of the history of message

In this first release, the goal is to do the connecting and contact discovery phase of the project. This includes;
  * The log-in and choosing of your own username
  * Verification that the chosen username is available on the network
  * Initial creation of the contact list, containing yourself and other users already online when the log-in happened
  * Dynamic update of the contact list when new user join on the same network

To run the project on your computer on;

WINDOWS: Open the terminal window (cmd), navigate to the project root directory and run 'mvn clean package'. Once the tests have passed and the compilation is complete, tap the command 'java -jar target/ChatSystem-jar-with-dependencies.jar'. This should start the program.

LINUX: Open the terminal window (bash), navigate to the project root directory and run 'mvn package' and 'mvn exec:java -Dexec.mainClass="cs.Main"'.

