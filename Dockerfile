
# Java 17 + Maven base image
FROM maven:3.10.0-amazoncorretto-17-debian-trixie

WORKDIR /app

# Install Chromium and its matching WebDriver
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium \
    chromium-driver \
    ca-certificates \
    fonts-liberation \
    && rm -rf /var/lib/apt/lists/*

# Browser and driver locations
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER_BIN=/usr/bin/chromedriver

# Enable headless execution inside the container
ENV MAVEN_OPTS="-Dheadless=true"

# Verify browser and driver installations
RUN "$CHROME_BIN" --version && "$CHROMEDRIVER_BIN" --version

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy application source and HTML resources
COPY src ./src

# Compile Java test code
RUN mvn -B test-compile

# Execute Selenium tests when container starts
CMD ["mvn", "-B", \
     "-Dexec.mainClass=com.example.tests.LocalLoginTest", \
     "-Dexec.classpathScope=test", \
     "exec:java"]
