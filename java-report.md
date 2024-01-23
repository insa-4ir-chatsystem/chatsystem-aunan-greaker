## `java-report.md`

### Tech Stack

- **UDP for Contact Discovery Phase:**
  - *Motivation:* We used UDP for the contact discovery phase because of its connectionless nature and the absence of acknowledgments. This is suitable for broadcasting messages to discover contacts on the network without the need for a reliable protocol with connection.

- **TCP for Messaging:**
  - *Motivation:* We chose TCP for sending actual messages, thus benefiting from its reliable and connection-oriented nature. This ensures the quality of service required for real-time communication, where message delivery and order are crucial.

- **SQLite for Message Storage:**
  - *Motivation:* The choice of SQLite for message storage was due to its lightweight, embedded nature. It offers a self-contained, serverless, and is a database not requiring configuration, making it a practical choice for a peer to peer chatsystem with local message storage thus not using a separate database server.

- **Java Swing for GUI:**
  - *Motivation:* Java Swing was used for the GUI, because it's part of Java's standard library and provides platform independence. It facilitates the creation of cross-platform, interactive user interfaces. However, it poses challenges in handling complexity and lacks some modern features found in newer UI frameworks. For a simple chat system, it was a suitable choice, but for a more complex application, we would consider using a more modern UI framework.

### Testing Policy

We used a Test-Driven Development (TDD) approach, where tests are created before the actual code. Our testing policy involved writing unit tests for individual components and integration tests for overall functionality. We used the continuous integration tool on GitHub to ensure automated testing on code changes. Additionally, we did manual tests to validate the user interface's responsiveness and behavior under different scenarios, assuring its functionally and usability.
