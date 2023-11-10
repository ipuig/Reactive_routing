compile:
	@javac -cp bin/ src/*.java -d bin/
	

endpoint:
	@java -cp bin Main 1 $(port)

router:
	@java -cp bin Main 0 $(port)
