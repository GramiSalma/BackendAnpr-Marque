# Utiliser l'image de base OpenJDK
FROM openjdk:17-jdk-slim

# Créer un répertoire pour l'application
WORKDIR /app

# Copier le fichier JAR dans le conteneur
COPY target/plaquesImmatriculation-0.0.1-SNAPSHOT.jar app.jar

# Spécifier le port d'écoute
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
