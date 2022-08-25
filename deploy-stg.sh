mvn clean install -DskipTests
scp target/khwish-backend-0.0.1.jar ../khwish-firebase-service-account.json ../khwish-backend-service-account.json run-stg.sh ssj@xxx.xx.x.xx:/home/ssj/khwish/
