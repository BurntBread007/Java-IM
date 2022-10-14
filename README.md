# Java-IM
### Host group chats for friends over a network, allowing for commands and multi-user collaboration.

## About The Project
This program is in Pre-Release form, meaning that this program is not fully finished; bugs may be present and features may be missing. If you encounter an issue, please consider posting an Issue so I can review and improve on this project.

I started to develop this program with the intent to use this as an instant messenger when other main forms of communication are not available. While this may not really be suited for lengthy or large use, it is a decent way to talk with other(s) (either across your network or over the internet if you port-forward) in short bursts. Overall, it is a simple little group chat client that connects with a server to other clients, allowing groups to collaborate. Other smaller features are available via text commands, which allows for renaming yourself on-the-fly, flipping a coin, and listing stats about the server.

## Installation & Run
**1.** Download [Java 8 JRE](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) if your computer does not already have this installed. To check, run `java -version` in a terminal. If "1.8" is printed, you're good to go!

**2.** Head to the [latest release](https://github.com/BurntBread007/Java-IM/releases) of this project, and download the zip file that suits your need. Most people will download the Client version, as this is the one that joins server chats and allows you to join as a user.

Once downloaded, open the folder. There are multiple ways to run this jar file, but the 2 relevant ones in my case are:

**1.** Windows Command Prompt - Run the .bat file, labeled "Run X - CMD", and voila! Alternatively, type `cmd.exe` into your file explorer's address bar, and in the newly created terminal window, type `java -jar X.jar`, where X is the name of the jar file.

**2.** Windows PowerShell - Hold Shift, and Right-Click on an empty spot in the file explorer. In the window menu that appears, press "Open PowerShell window here". With this open, type `java -jar X.jar`, where X is the name of the jar file.

**Note: To paste text into CMD, simply right-click within its window.**

## Client Usage
**1.** You are first prompted to enter a username, used to identify yourself in the chatroom, so enter a name accordingly.

**2.** Enter the IP of the computer you wish to join. Entering both local IPs, (192.168.X.X) and online IPs are compatible-- if the one you are reaching is port-forwarded correctly. If you may are given a "failed connection" prompt, check to make sure the IP was typed correctly, confirm that a server exists, then try again. **Type "localhost" or nothing at all to connect the client to a server on the same device.**

**3.** Enter the port number. If a server exists on that port, you will join the chatroom. If no server exists, the program will notify you to try a different port, or try again. Numbers 1 through 65000 are valid.

**4.** If everything worked well, _success_! You should now be connected to the chat room, free to type ~~almost~~ anything on your mind!

## Server Usage
**1.** The only manual step is to enter a port number to host the chatroom on. Users need this port number to join, so make sure to notify those joining of the number.

**2.** However, if you are expecting users to join over the Internet or from another network, your computer must be port-forwarded with the corresponding port you entered. This setup varies between modems and ISPs, seek help elsewhere.

## Edit and Compile Yourself
If you are interested in adding on to this project or branching this into your own variant, I have listed information below about my (probably) odd file structure and setup to this project.

**1.** First, make sure you have [Java 8 JDK](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) installed. You will not be able to compile java files with the traditional JRE. And while this project *might* run on Java 8+, I can not guarantee the best compatibility with 8+.

**2.** Download the latest repo of this project. For stability, download the source zips of any [release](https://github.com/BurntBread007/Java-IM/releases). If there are commits that are not present in releases that you know you want included, download the current source zip.

Now to the project itself. Below is a list about each folder:

**1.** \text\ - The folder that contains .TXTs of messages when a user joins and leaves. This also contains a Commands.TXT file that has descriptions of every command.

**2.** \java\ - The *real* project folder. This contains all 3 (as of v0.1.6) .JAVA files, which is then compiled with shortcuts within the root folder.

**3.** \classes\ - The compiled folder. This is where the compiled .CLASS files are saved to, and within here are 3 more folders. \text\ is a duplicate of the root's \text\, \manifests\ is a folder of .MF files used to include special additions to built .JARs, and finally \jars\ where the .JAR files are saved to, with included Windows PS shortcuts.

Making changes mainly involve editing/adding files into the root's \text\ and \java\ folders.

**a.** When ready to compile, run the shortcuts CompileX.BAT to compile the java files to classes and then immediately run said class. To package these classes into a jar, run the CreateJars.bat within the \classes\ folder.

**b.** Alternatively, run `javac ./java/(JAVA FILES) -d ./classes` to compile the java files to classes. To package these classes to a jar, run `jar cvfm0 ./jars/(JAR FILE) ./manifests/(MANIFEST FILE) ./text/*.txt (LIST CLASS FILES)`