# Chat System Project

## Overview

The Chat System Project is designed to connect users through a chat system, focusing on various phases of development. The primary goals include:

- Connecting and contact discovery phase
- Peer-to-peer messaging
- Implementation of a user-friendly chat system interface
- History of messages handling

For the initial release, the project concentrates on the connecting and contact discovery phase.

## Features in the First Release

1. **Log-in and Username Selection:**
   - Users can log in and choose their usernames.

2. **Username Availability:**
   - Verification ensures the chosen username is available on the network.

3. **Contact List Management:**
   - Initial creation of the contact list, including the user and others online during log-in.

## Prerequisites

Before running the program, ensure you have [Maven](https://maven.apache.org/) installed on your machine.

## How to Run

### Windows

1. Open the terminal window (cmd).
2. Navigate to the project root directory.
3. Run `mvn clean package`.
4. Once tests pass and compilation is complete, run `java -jar target/chatsystem-0.1-SNAPSHOT.jar`.

### Linux

1. Open the terminal window (bash).
2. Navigate to the project root directory.
3. Run `mvn clean package` and `mvn exec:java -Dexec.mainClass="chatsystem.Main"`.

This will start the program on your computer.
