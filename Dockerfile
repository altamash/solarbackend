# FROM openjdk:8-jdk-alpine
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
# ARG DEPENDENCY=target/dependency
# COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
# COPY ${DEPENDENCY}/META-INF /app/META-INF
# COPY ${DEPENDENCY}/BOOT-INF/classes /app
# ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]


# WORKDIR /app
# COPY ./target/solar-api-0.0.1-SNAPSHOT.jar /app
# ADD target/solar-api-0.0.1-SNAPSHOT.jar solar-api-0.0.1-SNAPSHOT.jar
# EXPOSE 8080

# CMD ["java", "-jar", "solar-api-0.0.1-SNAPSHOT.jar"]
# ENTRYPOINT ["java", "-jar", "solar-api-0.0.1-SNAPSHOT.jar"]


From openjdk:8
copy ./target/solar-api-0.0.1-SNAPSHOT.jar solar-api-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","solar-api-0.0.1-SNAPSHOT.jar"]
