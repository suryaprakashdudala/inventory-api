cls
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar target/inventory-api-0.0.1-SNAPSHOT.jar