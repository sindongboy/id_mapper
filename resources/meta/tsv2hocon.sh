#!/bin/bash
function usage() {
	echo "usage: $0 [options]"
	echo "-i	meta file"
	echo "-o	output file"
	exit 1
}

if [[ $# == 0 ]]; then 
	usage
fi

while test $# -gt 0;
do
	case "$1" in
		-h)
			usage
			;;
		-i)
			shift
			metafile=$1
			shift ;;
		-o)
			shift
			confile=$1
			shift ;;
		*)
			break
			;;
	esac
done 

if [[ -z ${metafile} ]] || [[ ! -f ${metafile} ]]; then
	echo "meta file not found: ${metafile}"
	usage
fi

if [[ -f ${confile} ]]; then 
	cp -f ${confile} ${confile}.bak
fi

metafile="./daum-tv-meta.tsv"
confile="./daum-meta.conf"
echo -e "daum {" > ${confile}
echo -e "	meta = [" >> ${confile}
count=1
total=`cat ${metafile} | wc -l | sed 's/^  *//g'`
while read line
do
	echo -e "${count} / ${total}"
	let count=count+1
	echo -e "		{" >> ${confile}
	id=`echo -e "${line}" | cut -f 1`
	echo -e "			id = \"${id}\"" >> ${confile}
	title=`echo -e "${line}" | cut -f 2`
	echo -e "			title = \"${title}\"" >> ${confile}
	bdate=`echo -e "${line}" | cut -f 3`
	echo -e "			begin = \"${bdate}\"" >> ${confile}
	edate=`echo -e "${line}" | cut -f 4`
	if [[ ${edate} == null ]]; then 
		echo -e "			end = false" >> ${confile}
	else
		echo -e "			end = true" >> ${confile}
	fi
	synopsis=`echo -e "${line}" | cut -f 5 | sed 's/\"//g'`
	echo -e "			synopsis = \"${synopsis}\"" >> ${confile}
	directors=`echo -e "${line}" | cut -f 6`
	echo -e "			directors = [" >> ${confile}
	echo -n "				" >> ${confile}
	for director in `echo -e ${directors} | sed 's/\^/ /g'`
	do
		echo -n "\"${director}\"," >> ${confile}
	done
	echo "" >> ${confile}
	echo -e "			]" >> ${confile}
	actors1=`echo -e "${line}" | cut -f 7`
	echo -e "			actors1 = [" >> ${confile}
	echo -n "				" >> ${confile}
	for actor1 in `echo -e ${actors1} | sed 's/\^/ /g'`
	do
		echo -n "\"${actor1}\"," >> ${confile}
	done
	echo "" >> ${confile}
	echo -e "			]" >> ${confile}
	actors2=`echo -e "${line}" | cut -f 8`
	echo -e "			actors2 = [" >> ${confile}
	echo -n "				" >> ${confile}
	for actor2 in `echo -e ${actors2} | sed 's/\^/ /g'`
	do
		echo -n "\"${actor2}\"," >> ${confile}
	done
	echo "" >> ${confile}
	echo -e "			]" >> ${confile}
	score=`echo -e "${line}" | cut -f 10`
	echo -e "			score = ${score}" >> ${confile}
	scoreCount=`echo -e "${line}" | cut -f 9`
	echo -e "			score-count = ${scoreCount}" >> ${confile}
	echo -e "		}" >> ${confile}
done < ${metafile}
echo -e "	]" >> ${confile}
echo -e "}" >> ${confile}
