compile:
	@javac -cp bin/ src/*.java -d bin/
	

endpoint:
	@java -cp bin Main 1

router:
	@java -cp bin Main 0
