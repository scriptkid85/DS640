#Usage of the makefile:
#Please adjust the configure file (./conf/mapred.conf) before start real jobs (run jobTracker first and write hostname to config file)
#
#
#Compile:
#	make clean
#	make
#
#Start the cluster:
#	on JobTracker machine:
#		1)run the command to start rmi registry: 
#			make run-registry
#		2)run the command to start jobtracker: 
#			make run-jt
#	on every TaskTracker machine:
#		  run the command to start tasktracker: 
#			make run-tt ADDR=? 
#			(where ? is the jobtracker machine's hostname)
#	
#Start jobs:
#	on any machine to submit the test job:
#		  run the command to submit the word count job: 
#			make run-wc IN=? OUT=? NRED=?
#			(where the first ? is the input file and second ? is the output directory, the third ? is the number of reduce tasks)
#		  run the command to submit the anagram-finding job:
#			make run-an IN=? OUT=? NRED=?
#			(where the first ? is the input file and second ? is the output directory, the third ? is the number of reduce tasks)
#		  run the command to submit the word count job with error:
#			make run-wcerror IN=? OUT=? NRED=?
#			(this is for testing failure)
#
#Job Management:
#	on any machine:
#		  run the command to check all runnning jobs' status:
#			make checkalljob
#		  run the command to check a runnning job' status:
#			make checkjob ID=?
#			(where ? is the job's id)
#		  run the command to killall runnning jobs:
#			make killalljob
#		  run the command to kill a runnning job:
#			make killjob ID=?
#			(where ? is the job's id)




.SUFFIXES: .java .class 
SOURCES = ../src/*.java 
CLASSES = -r cmu
JFLAGS = -g -classpath . -d .


.java.class:
	javac $(JFLAGS) $*.java

default:
	ant

all:
	ant

run-wc:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.test.WordCount $(IN) $(OUT) $(NRED)

run-wcerror:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.test.WordCountError $(IN) $(OUT) $(NRED)

run-an:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.test.Anagram $(IN) $(OUT) $(NRED)

run-jt:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.JobTracker -Djava.rmi.server.codebase=file:./

run-tt:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.TaskTracker $(ADDR)

checkjob:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.JobManager -check $(ID)

killjob:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.JobManager -kill $(ID)

checkalljob:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.JobManager -checkall

killalljob:
	java -cp . -Djava.security.policy=security.policy cmu.ds.mr.mapred.JobManager -killall

run-registry:
	rmiregistry &

clean:
	rm $(CLASSES)

doc: $(SOURCES)
	javadoc -author $(SOURCES)


