all:
	@echo "For running the preprocessor type:"
	@echo "time make preprocess < input.txt"
	@echo
	@echo "For running the query program type:"
	@echo "time make query < query.txt"
	@echo
JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        ./com/company/LineDetails.java \
        ./com/company/Preprocessor.java \
        ./com/company/QueryExecutor.java \
        ./com/company/Main.java 
default: classes

classes: $(CLASSES:.java=.class)

preprocess:
	java com.company.Main "preprocess"
	
query:
	java com.company.Main "query"
clean:
	$(RM) *.class
