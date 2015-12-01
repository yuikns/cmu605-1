JC = javac
JFLAGS = -encoding UTF-8
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

SOURCE_FILES = *.java 

default: classes

classes: $(SOURCE_FILES:.java=.class)

clean:
	$(RM) *.class
