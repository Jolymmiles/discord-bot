FROM openjdk:8
LABEL name="discord-bot"
COPY build/libs/discord-bot-1.4-jesys-all.jar /discord-bot-jesys.jar
CMD ["java", "-jar", "/discord-bot-jesys.jar"]