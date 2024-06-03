# Inventory-Management-System
Inventory Management System in Java (GUI w/ Swing, JDBC w/ SQLite, Multithreading, Multi-User Concurrency, Real-Time Action Broadcasting)

# Project Features
- GUI with Java Swing
- Database Integration with JDBC and SQLite
- Multi Threaded
- Multi User Concurrency
- Real Time Action Broadcasting
- Encryption
- Logging

# How The Project Works
The project implements an inventory management system. There is a server and multiple clients can be started at the same time. Due to multi-threading and multi-user concurrency, all the clients can connect to the server and have real time updates of the inventory by pressing the refresh button. The inventory is implemented using an SQLite database with JDBC and the ItemDAO allows for 3 operations, ADD, DELETE and UPDATE which are self explanatory. Whenever an operation is performed, the clients name, the operation and the item involved are broadcasted from the server to all other clients in real time and they are also logged in a text file that keeps track of all operations that are modifying the database. This is so that other clients can refresh and see the updates in real time and the database administrator can keep track of all the changes in the database. The GUI is implemented using Java SWING and it incorporates multiple panels that include a text area, a table model etc. and it allows for filtering based on different attributes like name, id, etc. The communication between the server and the clients is encrypted and in order to see the inventory and perform any operations you have to login to the server with a OTP only shown on the server text area for security.

# Project Display
## Image Depicting Display of Inventory, Add Operation, Action Broadcasting
![image](Images/ADD-BROADCASTING-DBS.png)

## Image Depecting OTP Verification
![image](Images/OPT%20Verificaiton.png)
