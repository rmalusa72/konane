JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		GameState.java \
		Move.java \
		Server.java \
		Agent.java \
		HumanAgent.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class