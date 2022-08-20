mvn clean install -DskipTests
scp target/khwish-backend-0.0.1.jar ../khwish-firebase-service-account.json ../khwish-backend-service-account.json run-stg.sh ssj@139.59.3.37:/home/ssj/khwish/