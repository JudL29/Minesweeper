#Base img for openjdk
FROM eclipse-temurin:17-jdk-jammy

# Install virtual display, window manager, VNC, noVNC, supervisord
#   Get package updates and installs:
#   Virtual framebuffer — a fake display screen
#   VNC server to read from fake display
#   Lightweight window manager, for resize and bar
#   Vnc to get the app to localhost on 8080 on the browser
#   Bridge for x11vnc to novnc
#   Process manager for the stuff above
#   cleans up installation
RUN apt-get update && apt-get install -y \
    xvfb x11vnc fluxbox novnc websockify x11-utils \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

#Cd to be in app
WORKDIR /app

#Copy files from src into container
COPY src/ ./src/

#Gets all .java files and compiles them into another directory /out
RUN javac $(find src -name "*.java") -d out

#Get the start file
COPY start.sh ./start.sh
RUN sed -i 's/\r//' start.sh
RUN chmod +x start.sh

#App would be on port 8080
EXPOSE 8080

# Run start
CMD ["./start.sh"]