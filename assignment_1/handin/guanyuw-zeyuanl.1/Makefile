
.SUFFIXES: .java .class 
SOURCES = ByteSender.java CannyEdgeDetector.java CommListener.java CommMasterListenThread.java CommMoveProcess.java CommSender.java CommSlaveListenThread.java EdgeProcess.java GzipProcess.java MigratableProcess.java Pair.java ProcessBalancer.java ProcessBalancerStrategist.java ProcessManager.java ProcessRunner.java RunningProcessTable.java Serializer.java SlaveChecker.java SlaveNotifier.java SlaveTable.java TestProcess.java TransactionalFileInputStream.java TransactionalFileOutputStream.java
CLASSES = ByteSender.class CannyEdgeDetector.class CommListener.class CommMasterListenThread.class CommMoveProcess.class CommSender.class CommSlaveListenThread.class EdgeProcess.class GzipProcess.class MigratableProcess.class Pair.class ProcessBalancer.class ProcessBalancerStrategist.class ProcessManager.class ProcessRunner.class RunningProcessTable.class Serializer.class SlaveChecker.class SlaveNotifier.class SlaveTable.class TestProcess.class TransactionalFileInputStream.class TransactionalFileOutputStream.class
JFLAGS = -g -classpath .

.java.class:
	javac $(JFLAGS) $*.java

default:
	javac -classpath . $(SOURCES)

all:
	javac -classpath . $(SOURCES)

demo:
	@echo 'Please open multiple terminal (e.g.1 master 2 slave)'
	@echo 'Usage: make master'
	@echo 'Usage: make masterport PORT=<port num>'
	@echo 'Usage: make slave NAME=<master name>'
	@echo 'Usage: make slaveport NAME=<master name> MP=<master port num> SP=<slave port num>'
	@echo 'Usage when running process: GzipProcess data/test.txt data/r1.gz'
	@echo 'Usage when running process: EdgeProcess data/img.jpg data/i1.jpg'

master:
	java ProcessManager
	
masterport:
	java ProcessManager -p $(PORT)

slave:
	java ProcessManager -c $(NAME) -mp 4444 -sp 4446

slaveport:
	java ProcessManager -c $(NAME) -mp $(MP) -sp $(SP)

clean:
	rm $(CLASSES)

doc: $(SOURCES)
	javadoc -author $(SOURCES)


