#!/bin/bash

# Make this script executable from terminal:
# chmod 755 version.sh
set -e # Any subsequent(*) commands which fail will cause the shell script to exit immediately

ROOT_DIR=$(git rev-parse --show-toplevel)
LINE="================================================================================"
VERSION_REGEX="[0-9]+\.[0-9]+\.[0-9]+"

GRADLE_PROPERTIES_FILE=$ROOT_DIR"/code/gradle.properties"

# NotificationBuilder files
NOTIFICATIONBUILDER_CONSTANTFILE=$ROOT_DIR"/code/notificationbuilder/src/main/java/com/adobe/marketing/mobile/notificationbuilder/NotificationBuilder.kt"
NOTIFICATIONBUILDER_CONSTANTFILE_VERSION_REGEX="^ +private const val VERSION *= *"

help()
{
   echo ""
   echo "Usage: $0 -n COMPONENT_NAME -v VERSION -d DEPENDENCIES"
   echo ""
   echo -e "    -v\t- Version to update or verify for the component. \n\t  Example: 3.0.2\n"
   echo -e "    -d\t- Comma seperated dependecies to update along with their version. \n\t  Example: "Core 3.1.1, Edge 3.2.1"\n"
   echo -e "    -u\t- Updates the version. If this flag is absent, the script verifies if the version is correct\n"
   exit 1 # Exit script after printing help
}

sed_platform() {
    # Ensure sed works properly in linux and mac-os.
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "$@"
    else
        sed -i "$@"
    fi
}

update() {
    echo "Changing $NAME version to $VERSION"

    # Replace version in Constants file
    echo "Changing 'VERSION' to '$VERSION' in '$CONSTANTS_FILE'"    
    sed_platform -E "/$CONSTANTS_FILE_VERSION_REGEX/{s/$VERSION_REGEX/$VERSION/;}" $CONSTANTS_FILE

    # Replace version in gradle.properties
    echo "Changing $GRADLE_TAG to '$VERSION' in '$GRADLE_PROPERTIES_FILE'"
    sed_platform -E "/^$GRADLE_TAG/{s/$VERSION_REGEX/$VERSION/;}" $GRADLE_PROPERTIES_FILE  

    # Replace dependencies in gradle.properties
    if [ "$DEPENDENCIES" != "none" ]; then
        IFS="," 
        dependenciesArray=($(echo "$DEPENDENCIES"))

        IFS=" "
        for dependency in "${dependenciesArray[@]}"; do
            dependencyArray=(${dependency// / })
            dependencyName=${dependencyArray[0]}
            dependencyVersion=${dependencyArray[1]}

            if [ "$dependencyVersion" != "" ]; then
                echo "Changing 'maven${dependencyName}Version' to '$dependencyVersion' in '$GRADLE_PROPERTIES_FILE'"            
                sed_platform -E "/^maven${dependencyName}Version/{s/$VERSION_REGEX/$dependencyVersion/;}" $GRADLE_PROPERTIES_FILE  
            fi        
        done
    fi
}

verify() {    
    echo "Verifing $NAME version is $VERSION"

    if ! grep -E "$CONSTANTS_FILE_VERSION_REGEX\"$VERSION\"" "$CONSTANTS_FILE" >/dev/null; then
        echo "'VERSION' does not match '$VERSION' in '$CONSTANTS_FILE'"            
        exit 1
    fi

    if ! grep -E "^$GRADLE_TAG=.*$VERSION" "$GRADLE_PROPERTIES_FILE" >/dev/null; then
        echo "'version' does not match '$VERSION' in '$GRADLE_PROPERTIES_FILE'"            
        exit 1
    fi

    if [ "$DEPENDENCIES" != "none" ]; then
        IFS="," 
        dependenciesArray=($(echo "$DEPENDENCIES"))

        IFS=" "
        for dependency in "${dependenciesArray[@]}"; do
            dependencyArray=(${dependency// / })
            dependencyName=${dependencyArray[0]}
            dependencyVersion=${dependencyArray[1]}

            if [ "$dependencyVersion" != "" ]; then
                if ! grep -E "^maven${dependencyName}Version=.*$dependencyVersion" "$GRADLE_PROPERTIES_FILE" >/dev/null; then
                    echo "maven${dependencyName}Version does not match '$dependencyVersion' in '$GRADLE_PROPERTIES_FILE'"
                    exit 1
                fi
            fi        
        done
    fi

    echo "Success"
}


while getopts "n:v:d:u" opt
do
   case "$opt" in
      n ) NAME="$OPTARG" ;;    
      v ) VERSION="$OPTARG" ;;
      d ) DEPENDENCIES="$OPTARG" ;;
      u ) UPDATE="true" ;;   
      ? ) help ;; # Print help in case parameter is non-existent
   esac
done

# Print help in case parameters are empty
if [ -z "$NAME" ] || [ -z "$VERSION" ]
then
   echo "********** USAGE ERROR **********"
   echo "Some or all of the parameters are empty. See usage below:";
   help
fi


NAME_LC=$(echo "$NAME" | tr '[:upper:]' '[:lower:]')
NAME_UC=$(echo "$NAME" | tr '[:lower:]' '[:upper:]')

eval CONSTANTS_FILE=\$$"$NAME_UC"_CONSTANTFILE
eval CONSTANTS_FILE_VERSION_REGEX=\$$"$NAME_UC"_CONSTANTFILE_VERSION_REGEX
GRADLE_TAG="$NAME_LC"Version

echo "$LINE"
if [[ ${UPDATE} = "true" ]];
then
    update 
else 
    verify
fi
echo "$LINE"
