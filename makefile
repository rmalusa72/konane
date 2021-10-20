JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		GameState.java \
		Move.java \
		Konane.java \
		Agent.java \
		HumanAgent.java \
		RandomAgent.java \
		MinimaxAgent.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class