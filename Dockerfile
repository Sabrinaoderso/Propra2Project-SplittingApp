FROM gradle:8.0.2-jdk19

# Kopieren Sie die ausführbare JAR-Datei Ihrer Spring Boot-Anwendung in das Docker-Image
COPY . /app/

# Setzen Sie die Arbeitsumgebung
WORKDIR /app

RUN chown -R gradle:gradle /app

# Expose the default port for Spring Boot web applications
EXPOSE 9000
RUN gradle clean build
# run the wait-for-it.sh
ENTRYPOINT ["sh", "wait-for-it.sh"]
