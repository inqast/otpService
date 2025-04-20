build:
	mvn clean package spring-boot:repackage

run:
	java -jar target/otp-1.0-SNAPSHOT.jar

migrate:
	mvn -Dflyway.configFiles=flyway.conf flyway:migrate

up:
	docker compose up -d --build

down:
	docker compose down

down-v:
	docker compose down -v
