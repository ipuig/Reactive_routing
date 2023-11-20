compile:
	./mvnw compile

endpoint:
	@java -cp ./target/classes ie.tcd.scss.Application endpoint

router:
	@java -cp ./target/classes ie.tcd.scss.Application router

app:
	@java -cp ./target/classes ie.tcd.scss.Application app
