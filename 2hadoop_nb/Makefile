JC = javac
JFLAGS = -target 1.6 -classpath hadoop-core-1.0.1.jar -d .
#JFLAGS = -classpath hadoop-core-1.0.1.jar -d .
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

SOURCE_FILES = \
	WordCount.java \
	run.java

default: classes

classes: $(SOURCE_FILES:.java=.class)

clean:
	$(RM) *.class *.zip
