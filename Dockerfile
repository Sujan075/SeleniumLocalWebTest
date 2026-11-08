# Use Maven with Java 17
FROM maven:3.10.0-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Install Chromium and ChromeDriver
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium \
    chromium-driver \
    && rm -rf /var/lib/apt/lists/*

# Set Chrome binary path
ENV CHROME_BIN=/usr/bin/chromium

# Copy Maven configuration
COPY pom.xml .

# Pre-fetch dependencies
RUN mvn dependency:go-offline -B

# Copy Java source code
COPY src ./src

# Copy HTML webpage
COPY webpage ./webpage

# Copy TestNG configuration
COPY testng.xml .

# Execute Selenium TestNG tests
CMD ["mvn", "clean", "test", "-Dsurefire.suiteXmlFiles=testng.xml"]