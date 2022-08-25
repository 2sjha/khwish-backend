mvn clean install -DskipTests
scp target/khwish-backend-0.0.1.jar ../khwish-firebase-service-account.json ../khwish-backend-service-account.json run-prod.sh ssj@xxx.xx.xx.xxx:/home/ssj/khwish/
