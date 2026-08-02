GRADLE := ./gradlew

setup:
	$(GRADLE) build

app:
	$(GRADLE) bootRun --args='--spring.profiles.active=development'

backend: app

clean:
	$(GRADLE) clean

build:
	$(GRADLE) clean build

reload-classes:
	$(GRADLE) -t classes

start-prod:
	$(GRADLE) bootRun --args='--spring.profiles.active=production'

format:
	$(GRADLE) spotlessApply

lint:
	$(GRADLE) spotlessCheck

test:
	$(GRADLE) test

check:
	$(GRADLE) check

coverage:
	$(GRADLE) jacocoTestReport

.PHONY: setup app backend clean build reload-classes start-prod format lint test check coverage
