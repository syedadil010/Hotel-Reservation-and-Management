# OnlineBookingSystem

### Environment Setup
#### Eclipse

1. In your project directory, use the 'pull' command to make sure you have the latest version of the project

2. Open Eclipse. Navigate to File -> Import -> Maven -> existing maven projects

3. Browse to the project folder and click open

4. Select the project in the window and click finish.

At this point the project should open in Eclipse!
Let me know if this needs updating or if there are issues.


#### To run the project from source in Eclipse:
When you want to run the project, save your work in Eclipse and open the project's home folder from the terminal. Type
```shell
mvn clean package
```
There are heaps of other commands and tools available through Maven, but this will create a new version of the project in a .jar file. It can be found in the /target folder.



#### to build a jar from source
Make sure you have Maven installed
Navigate to project folder
Run mvn clean package
The jar will be deployed under \target\OnlineBookingSystem

### Running the project

#### To run the project using the provided JAR file.
1. Check that your computer has port 8090 free, if not, find a free port.
2. Make sure you have Java installed. (Tested to work with version 1.8)
2. Locate the OnlineBookingSystem.jar file in Command Prompt, Powershell, Terminal or Bash Shell.
3. cd into the directory containing OnlineBookingSystem.jar and make sure the ./SQL folder is present.
4. If port 8090 is free and you'd like to host the site on port 8090, execute:
   java -jar OnlineBookingSystem.jar
5. If port 8090 is not free OR you'd like to host the site on a different port, port: XXXX then execute:
   java -jar OnlineBookingSystem.jar --server.port=XXXX
6. Open your faviourate web browser and navigate to http://localhost:8090 or http://localhost:XXXX to view the landing/login page.
