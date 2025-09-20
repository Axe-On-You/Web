#!/bin/sh
# Simple daemon manager for Java FastCGI external server

JAVA="/usr/local/bin/java"
APP_JAR="/home/studs/s466730/httpd-root/fcgi-bin/labwork1.jar"
FCGI_PORT="24037"  # выберите свободный порт >1024, не равный Listen Apache
PID_FILE="/home/studs/s466730/httpd-root/java-fcgi.pid"
LOG_FILE="/home/studs/s466730/httpd-root/java-fcgi.log"

start() {
  if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    echo "Already running with PID $(cat "$PID_FILE")"
    exit 0
  fi

  echo "Starting Java FastCGI server on port $FCGI_PORT ..."
  nohup "$JAVA" -DFCGI_PORT="$FCGI_PORT" -jar "$APP_JAR" >> "$LOG_FILE" 2>&1 &
  echo $! > "$PID_FILE"
  sleep 1

  if kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    echo "Started. PID $(cat "$PID_FILE"). Logs: $LOG_FILE"
  else
    echo "Failed to start. See logs: $LOG_FILE"
    [ -f "$PID_FILE" ] && rm -f "$PID_FILE"
    exit 1
  fi
}

stop() {
  if [ -f "$PID_FILE" ]; then
    PID="$(cat "$PID_FILE")"
    if kill -0 "$PID" 2>/dev/null; then
      echo "Stopping PID $PID ..."
      kill "$PID"
      # Подождем до 10 секунд, затем SIGKILL
      for i in $(seq 1 10); do
        if kill -0 "$PID" 2>/dev/null; then
          sleep 1
        else
          break
        fi
      done
      if kill -0 "$PID" 2>/dev/null; then
        echo "Force killing PID $PID ..."
        kill -9 "$PID"
      fi
    fi
    rm -f "$PID_FILE"
    echo "Stopped."
  else
    echo "Not running."
  fi
}

status() {
  if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    echo "Running. PID $(cat "$PID_FILE"). Port $FCGI_PORT. Log: $LOG_FILE"
  else
    echo "Not running."
  fi
}

restart() {
  stop
  start
}

case "$1" in
  start) start ;;
  stop) stop ;;
  restart) restart ;;
  status) status ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
    ;;
esac