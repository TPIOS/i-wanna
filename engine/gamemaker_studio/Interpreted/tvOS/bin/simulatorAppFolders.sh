#! /bin/bash
 
# DHC - Hacked to always return a 0 code when successful.
 
SIM_DEVICES_FOLDER=~/Library/Developer/CoreSimulator/Devices
SIM_RUNTIME_PRFIX=com.apple.CoreSimulator.SimRuntime
 
function usage () {
  echo "simulatorAppFolders (-s <name> | -d <device-type>) [-?] [-i <iOS version>] [-a <app-name>]"
  echo "-? show help, and prints simulators currently installed"
  echo "-s <simulator name> the name of the device as it appears in Xcode's device list. Prints the folder of this simulator without -i will choose the first matching folder"
  echo "-d <simulator device type> the device type as it appears in the simulator plist file. Prints the folder of this simulator without -i will choose the first matching folder"
  echo "-i <iOS version> finds the folder matching the iOS version as well as the simulator name"
  echo "-p Used with -s to print the installed apps on a simulator"
  echo "-a <application name> Used with -s to print the application and data folder of the application"
  echo ""
  echo "simulator names:"
  cd ${SIM_DEVICES_FOLDER}
 
  for dir in *
  do
      DEVICE=$(/usr/libexec/PlistBuddy -c "print :name" ${dir}/device.plist;)
      RUNTIME=$(/usr/libexec/PlistBuddy -c "print :runtime" ${dir}/device.plist)
      DEVICETYPE=$(/usr/libexec/PlistBuddy -c "print :deviceType" ${dir}/device.plist)
      iOS_VER="${RUNTIME##*.}"
      device_type="${DEVICETYPE##*.}"
      echo "iOS: ${iOS_VER}, deviceType: ${device_type}, name: ${DEVICE}"
  done
  exit 1
}
 
SIMULATOR_NAME_ARG=""
APPLICATION_ARG=""
iOS_VERSION_ARG=""
DEVICE_TYPE_ARG=""
 
OPTIND=1
 
while getopts 's:d:a:i:?p' opt
do
  case "$opt" in
      s) SIMULATOR_NAME_ARG="${OPTARG}"
   ;;
      d) DEVICE_TYPE_ARG="${OPTARG}"
     ;;
      a)
        APPLICATION_ARG=${OPTARG}
      ;;
      i) iOS_VERSION_ARG=${OPTARG}
      ;;
      p) PRINT_APPS_ARG=true
      ;;
      ?) usage
      ;;
  esac
done
 
shift $(( OPTIND - 1 ))
 
if [[ "${SIMULATOR_NAME_ARG}" == "" && "${DEVICE_TYPE_ARG}" == "" ]]; then
   usage
fi
 
cd ${SIM_DEVICES_FOLDER}
 
for dir in *
do
  simName=$(/usr/libexec/PlistBuddy -c "print :name" ${dir}/device.plist)
  simRuntime=$(/usr/libexec/PlistBuddy -c "print :runtime" ${dir}/device.plist)
  simDeviceType=$(/usr/libexec/PlistBuddy -c "print :deviceType" ${dir}/device.plist)
  iOSVersion="${simRuntime##*.}"
  iOSDeviceType="${simDeviceType##*.}"
 
  if [[ ("${SIMULATOR_NAME_ARG}" == "${simName}" || "${DEVICE_TYPE_ARG}" == "${iOSDeviceType}" ) && ("${iOS_VERSION_ARG}" == "" || "${iOSVersion}" == "${iOS_VERSION_ARG}") ]]; then
 
    DEVICE_FOLDER="${SIM_DEVICES_FOLDER}/${dir}"
 
    if [[ "${APPLICATION_ARG}" == "" && ! "$PRINT_APPS_ARG" ]]; then
      echo "${DEVICE_FOLDER}"
      exit 0
    fi
  fi
       
done
 
DATA_FOLDER=${DEVICE_FOLDER}/data
 
if [ ! -d "$DATA_FOLDER/Containers" ]; then
   echo "There are no applications built for this simulator: ${SIMULATOR_NAME_ARG} ${DEVICE_TYPE_ARG} ${iOS_VERSION_ARG}"
   exit 1
fi
 
APPLICATION_FOLDER=${DEVICE_FOLDER}/data/Applications
 
function findAppFolder () {
  cd $1
  APP_FOLDER=""
  for dir in *
  do
    if [ -d $1/${dir}/${APPLICATION_ARG}.app ]; then
      APP_FOLDER=$1/${dir}
      break;
    fi
 
  done
}
 
function printAppsInFolder () {
  cd $1
  for dir in *
  do
    for dir in  $1/${dir}/*.app
    do
      y=${dir%.app}
      basename ${y}
    done
 
  done
}
 
if [ -d "${APPLICATION_FOLDER}" ]; then
   #We have the applications folder this means we are likely in an iOS 7 style application
   
   if [ "${PRINT_APPS_ARG}" == true ]; then
     printAppsInFolder ${APPLICATION_FOLDER}
   fi
 
   if [ "${APPLICATION_ARG}" == "" ]; then
       exit 1
   fi
 
   findAppFolder ${APPLICATION_FOLDER}
 
   if [ "${APP_FOLDER}" == ""]; then
     echo "We couldn't find the applications folder for this app."
     exit 1
   else
     echo "${APP_FOLDER}"
   fi
fi
 
CONTAINER_FOLDER=${DEVICE_FOLDER}/data/Containers
 
if [ "${PRINT_APPS_ARG}" == true ]; then
  printAppsInFolder ${CONTAINER_FOLDER}/Bundle/Application
fi
 
if [ "${APPLICATION_ARG}" == "" ]; then
  exit 1
fi
 
findAppFolder ${CONTAINER_FOLDER}/Bundle/Application
 
if [ "${APP_FOLDER}" = "" ]; then
  echo "We couldn't find the applications folder for this app."
  exit 1
else
  echo "App folder = ${APP_FOLDER}"
fi
 
# We need to get the application bundle idnetifier and use this to find the correct dataFolder.
BUNDLE_IDENTIFIER=$(/usr/libexec/PlistBuddy -c "print :CFBundleIdentifier" ${APP_FOLDER}/${APPLICATION_ARG}.app/Info.plist)
 
APPLICATION_DATA_FOLDER=${CONTAINER_FOLDER}/Data/Application
cd ${APPLICATION_DATA_FOLDER}
 
for dir in *
do
  IDENTIFIER=$(/usr/libexec/PlistBuddy -c "print :MCMMetadataIdentifier" ${dir}/.com.apple.mobile_container_manager.metadata.plist)
  if [ "${IDENTIFIER}" == "${BUNDLE_IDENTIFIER}" ]; then
    echo "dataFolder = ${APPLICATION_DATA_FOLDER}/${dir}"
    exit 0
  fi
 
done
 
echo "Couldn't find data folder"