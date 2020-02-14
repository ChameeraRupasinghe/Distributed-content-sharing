The project containes 3 sub projects:

1. Distributed-Content-Sharing
2. file-server-for-distributed-sharing
3. Bootstrap Server


1. Distributed-Content-Sharing
------------------------------
It containes the artifcats to run the nodes. Each node should run with file-server application (2. file-server-for-distributed-sharing)

Steps to run node:

#step1: Using terminal navigate to the project (Distributed-Content-Sharing) folder.
#step2: Use javac command to compile the project files

	$ javac -d out -sourcepath src src/com/distributed/Main.java

#step3: Navigate to out folder
#step4: Use java command to run the project.

	$ java com.distributed.Main <user_name> <port>

	Ex:
	$ java com.distributed.Main tharanga 9598

** A premade script files are available to for easiness


2. file-server-for-distributed-sharing
--------------------------------------
It containes the artifacts of the file server related to particular node. 

to run:

#step1: Go inside the project (file-server-for-distributed-sharing) folder.
#step2: add the following line to the file "src/main/resources/application.properties"
	to change the port number runnig.

	Ex: server.port=1234

#step3: go back to the main folder. Then, run below command
	
	$ mvn spring-boot:run

3. Bootstrap Server
-------------------

Bootstrap project. Use to register nodes.

Steps to run:

#step1: Go inside the Java folder inside the project (Bootstrap Server) folder
#step2: Run below two commands

	$ javac -d . -sourcepath src BootstrapServer.java
	$ java BootstrapServer
