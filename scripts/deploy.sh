#!/bin/sh


# bash colors
yellow=$(tput setaf 3)
green=$(tput setaf 2)
red=$(tput setaf 1)
normal=$(tput sgr0)
# get current directory name
BASEDIR=`dirname $0`
# assumes that next gradle commands will execute in basedir/..
# put current dir in the stack and move current working dir to basedir/..
# stack -> ./scripts ---- /.../.../px-android/
command pushd "$BASEDIR/.." > /dev/null

modules=('px-checkout' 'px-services' 'px-testlib' 'testlib')

# in order to make it a little bit more interactive while PID is running show loading.
function showSpinner {
  local -r pid="${1}"
  local -r delay='0.3'
  local spinstr='\|/-'
  local temp
  while ps a | awk '{print $1}' | grep -q "${pid}"; do
    temp="${spinstr#?}"
    printf "${yellow} [%c]  ${normal}" "${spinstr}"
    spinstr=${temp}${spinstr%"${temp}"}
    sleep "${delay}"
    printf "\b\b\b\b\b\b"
  done
  printf "    \b\b\b\b"
}

# upload the $1 param module, show spinner while PID running
function uploadModule {
  local pid
  echo "Uploading $1"
  ./gradlew -Pdeploy $1:bintrayUpload > /dev/null || echo "${red}error uploading $1 ${normal}" & export pid=$!
  showSpinner $pid
}

echo "${green}######### Init deploy #########${normal}"
./gradlew -Pdeploy clean
./gradlew -Pdeploy assemble

for current in ${modules[@]}
do
  uploadModule $current
done

echo "${green}######### deploy finished #########${normal}"
