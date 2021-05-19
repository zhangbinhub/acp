#!/bin/bash
WORK_PATH=$(cd "$(dirname "$0")";pwd)
APP_NAME=$WORK_PATH/xxxx-1.0.0.jar
JVM_PARAM='-server -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms256m -Xmx512m -Dfile.encoding=utf-8'
JVM_EXT_PARAM=@jvmExtParam@
cd $WORK_PATH

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WORK_PATH/libs

usage() {
  echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
  exit 1
}

is_exist(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  if [ -z "${pid}" ]; then
    return 1
  else
    return 0
  fi
}

start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
  else
    nohup $JAVA_HOME/bin/java $JVM_PARAM -jar $APP_NAME > /dev/null 2>&1 &
  fi
}

stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill $pid
  else
    echo "${APP_NAME} is not running"
  fi
}

stopNow(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
    sleep 1s
  else
    echo "${APP_NAME} is not running"
  fi
}

status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. pid is ${pid}"
  else
    echo "${APP_NAME} is not running."
  fi
}

restart(){
  stop
  for i in $(seq 1 30)
  do
    if [ $i -eq 30 ]; then
      stopNow
      start
      break
    else
      is_exist
      if [ $? -eq "0" ]; then
        sleep 1s
      else
        start
        break
      fi
    fi
  done
}

case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "stopNow")
    stopNow
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac
