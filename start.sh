#!/bin/bash

# Start Xvfb and wait until it's actually ready
Xvfb :1 -screen 0 1024x768x16 &
echo "Waiting for Xvfb..."
until xdpyinfo -display :1 > /dev/null 2>&1; do
    sleep 0.5
done
echo "Xvfb is ready"

# Now start everything else
export DISPLAY=:1
fluxbox &
x11vnc -display :1 -nopw -forever -shared -noxdamage &
websockify --web=/usr/share/novnc/ 8080 localhost:5900 &

# Start Java app
java -cp /app/out App