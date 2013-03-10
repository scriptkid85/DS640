
.SUFFIXES: .java .class 
SOURCES = ../src/*/*.java
CLASSES = ./*.class
JFLAGS = -g -classpath .

.java.class:
	javac $(JFLAGS) $*.java -d ./

default:
	javac -classpath . $(SOURCES) -d ./

all:
	javac -classpath . $(SOURCES) -d ./

demo:
	@echo 'Please open multiple terminal (e.g.1 reg 2 servers 2 clients). make in order (1st:reg, 2nd:server, 3rd:client). Can type wrong service name to see RemoteException'
	@echo 'Usage: make reg REGPORT=<registry port num>'
	@echo 'Usage: make server REGNAME=<registry name> REGPORT=<registry port num> SRVNAME=<service name> SERVERPORT=<server listen port>'
	@echo 'Usage: make client REGNAME=<registry name> REGPORT=<registry port num> SRVNAME=<service name>'
	# sample usage:
	# make reg REGPORT=4444
	# make server REGNAME=128.237.170.166 REGPORT=4444 SRVNAME=ZipCodeServer SERVERPORT=4545
	# make server REGNAME=128.237.170.166 REGPORT=4444 SRVNAME=ZC SERVERPORT=5555
	# make client REGNAME=128.237.170.166 REGPORT=4444 SRVNAME=ZipCodeServer
	# make client REGNAME=128.237.170.166 REGPORT=4444 SRVNAME=ZC
	# make client REGNAME=128.237.170.166 REGPORT=4444 SRVNAME=IntentionallyWrongName

client: 
	java testzip.ZipCodeClient $(REGNAME) $(REGPORT) $(SRVNAME) ./data.txt

reg:
	java registry.RMIRegistry $(REGPORT)

server:
	java ror.RMIServer testzip.ZipCodeServerImpl $(REGNAME) $(REGPORT) $(SRVNAME) $(SERVERPORT)

clean:
	rm $(CLASSES)

doc: $(SOURCES)
	javadoc -author $(SOURCES)


