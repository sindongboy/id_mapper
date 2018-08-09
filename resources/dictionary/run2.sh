#!/bin/bash

nmeta="/Users/sindongboy/Documents/workspace/id-mapper/resources/meta/naver.meta"
count=1
total=`cat ./bbb | wc -l | sed 's/^  *//g'`
while read line 
do	
	echo "${count}/${total}"
	let count=count+1
	title=`cat ${nmeta} | grep "^${line}	" | head -1 | cut -f 2`
	if [[ -z ${title} ]]; then
		echo -e "null	null" >> naver.bbb
		continue
	fi
	otitle=`cat ${nmeta} | grep "^${line}	" | head -1 | cut -f 3`
	if [[ -z ${otitle} ]]; then
		echo "${title}	null" >> naver.bbb
	else
		echo "${title}	${otitle}" >> naver.bbb
	fi
done < ./bbb
