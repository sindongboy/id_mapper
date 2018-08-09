#!/bin/bash

# -------------------------------------------- #
# ID Mapping Driver
# support : hoppin, naver, kmdb, tstore
# -------------------------------------------- #

function interactiveMode() {
    echo -n "continue? (y|N) [y]: "
    read cont
    if [ -z ${cont} ]; then
        echo "$0 - Continued!"
    elif [ ${cont} == "y" ]; then
        echo "$0 - Continued!"
    elif [ ${cont} == "N" ]; then
        echo "$0 - Terminated!"
        exit 1
    else
        interactiveMode
    fi
}

# env 
CONFIG="/Users/sindongboy/Documents/workspace/id-mapper/config"
DICT="/Users/sindongboy/Documents/workspace/id-mapper/resources/dictionary"
META="/Users/sindongboy/Documents/workspace/id-mapper/resources/meta"
OUT=""

# run
TYPE=""

# help 
function usage() {
	echo "Usage: $0 [options]"
	echo "options: [r] ==> required"
	echo "-h, --help                show help"
	echo "-t, --type=[HOPPIN|NAVER|KMDB|TSTORE]      specify run type [r]"
	echo "-c, --config=[configuration directory]      specify where the configurations located"
	echo "-d, --dict=[dictionary directory]      specify where the dictionaries located"
	echo "-m, --meta=[meta directory]      specify where the meta located"
	echo "-o, --out=[output directory]		specify output file"
	exit 0
}

if [ $# == 0 ]; then
	usage
fi

while test $# -gt 0; do
	case "$1" in
		-h|--help)
			usage
			;;
		-t)
			shift
			if test $# -gt 0; then
				if [[ ! $1 == "HOPPIN" && ! $1 == "NAVER" && ! $1 == "KMDB" && ! $1 == "TSTORE" ]]; then
					echo "[ERROR] Run type must be HOPPIN | NAVER | KMDB | TSTORE"
					usage
				fi
				TYPE=$1
			else
				echo "no run type specified, must be HOPPIN | NAVER | KMDB | TSTORE"
				exit 1
			fi
			shift
			;;
		--type*)
			TYPE=`echo $1 | sed -e 's/^[^=]*=//g'`
			if [[ ! ${TYPE} == "HOPPIN" && ! ${TYPE} == "NAVER" && ! ${TYPE} == "KMDB" && ! ${TYPE} == "TSTORE" ]]; then
				echo "[ERROR] Run type must be HOPPIN | NAVER | KMDB | TSTORE"
				usage
			fi
			shift
			;;
		-c)
			shift
			if test $# -gt 0; then
				CONFIG=$1
			else
				echo "argument needed : configuration directory"
				exit 1
			fi
			shift
			;;
		--config*)
			CONFIG=`echo $1 | sed -e 's/^[^=]*=//g'`
			shift
			;;
		-d)
			shift
			if test $# -gt 0; then
				DICT=$1
			else
				echo "argument needed : dictionary directory"
				exit 1
			fi
			shift
			;;
		--dict*)
			DICT=`echo $1 | sed -e 's/^[^=]*=//g'`
			shift
			;;
		-m)
			shift
			if test $# -gt 0; then
				META=$1
			else
				echo "argument needed : meta directory"
				exit 1
			fi
			shift
			;;
		--meta*)
			META=`echo $1 | sed -e 's/^[^=]*=//g'`
			shift
			;;
		-o)
			shift
			if test $# -gt 0; then
				OUT=$1
			else
				echo "argument needed : output directory"
				exit 1
			fi
			shift
			;;
		--out*)
			OUT=`echo $1 | sed -e 's/^[^=]*=//g'`
			shift
			;;

		*)
			break
			;;
	esac
done




# LIB
GUAVA="${HOME}/.m2/repository/com/google/guava/guava/10.0.1/guava-10.0.1.jar"
if [[ ! -f ${GUAVA} ]]; then
	echo -n "dependency error : "
	echo ${GUAVA} | grep -o "\/[-a-zA-Z0-9.]*\.jar"
	exit 1
fi
LOG4J="${HOME}/.m2/repository/log4j/log4j/1.2.7/log4j-1.2.7.jar"
if [[ ! -f ${LOG4J} ]]; then
	echo -n "dependency error : "
	echo ${LOG4J} | grep -o "\/[-a-zA-Z0-9.]*\.jar"
	exit 1
fi
OMPCONFIG="/Users/sindongboy/.m2/repository/com/skplanet/nlp/omp-config/1.0.6-SNAPSHOT/omp-config-1.0.6-SNAPSHOT.jar"
if [[ ! -f ${OMPCONFIG} ]]; then
	echo -n "dependency error : "
	echo ${OMPCONFIG} | grep -o "\/[-a-zA-Z0-9.]*\.jar"
	exit 1
fi

CLI="/Users/sindongboy/.m2/repository/com/skplanet/nlp/cli/1.0.0/cli-1.0.0.jar"
COMMONCLI="/Users/sindongboy/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar"

# TARGET
VERSION=`ls -1t ../target | grep "jar" | head -1 | sed 's/id-mapper-//g' | sed 's/\.jar//g'`

TARGET="../target/id-mapper-${VERSION}.jar"
if [[ ! -f ${TARGET} ]]; then
	echo "[ERROR] No Target Found, now building project"
	cd ../
	mvn install
	cd -
fi

# Classpath
CP="$CONFIG:$DICT:$META:$OMPCONFIG:$LOG4J:$GUAVA:$CLI:$COMMONCLI:$TARGET"

echo "# =========================== #"
echo "     RUN CONFIGURATIONS"
echo "# =========================== #"
echo "target version : ${VERSION}"
echo "configuration path : ${CONFIG}"
echo "dictionary path : ${DICT}"
echo "meta path : ${META}"
echo "run type : ${TYPE}"
echo "output : ${OUT}"
#interactiveMode

if [[ ${TYPE} == "HOPPIN" ]]; then 
	if [[ ! -z ${OUT} ]]; then
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.HoppinMapperDriver > ${OUT}
	else
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.HoppinMapperDriver
	fi
elif [[ ${TYPE} == "NAVER" ]]; then 
	if [[ ! -z ${OUT} ]]; then
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.NaverMapperDriver -m ${OUT} -p ./naver.pair
	else
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.NaverMapperDriver
	fi
elif [[ ${TYPE} == "TSTORE" ]]; then 
	if [[ ! -z ${OUT} ]]; then
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.TstoreMapperDriver > ${OUT}
	else
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.TstoreMapperDriver
	fi
else 
	if [[ ! -z ${OUT} ]]; then
		java -Xmx8G -Dfile.encoding="UTF-8" -cp ${CP} com.skplanet.nlp.driver.KMDBMapperDriver -u ${OUT} -m ../resources/meta/kmdb.pair
	fi
fi

