FROM anapsix/alpine-java
COPY green-hill-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-server","-Xms1024m","-Xmx1024m","-Xss256k","-XX:-TieredCompilation","-XX:+UseStringDeduplication","-XX:+UseG1GC","-XX:MaxGCPauseMillis=200","-XX:ParallelGCThreads=20","-XX:ConcGCThreads=5","-XX:InitiatingHeapOccupancyPercent=70","-jar","/app.jar"]