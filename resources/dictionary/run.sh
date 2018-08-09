#!/bin/bash

hmeta="/Users/sindongboy/Documents/workspace/id-mapper/resources/meta/hoppin.meta"
count=1
total=`cat ./aaa | wc -l | sed 's/^  *//g'`
while read line 
do	
	echo "${count}/${total}"
	let count=count+1
	title=`cat ${hmeta} | grep "^${line}" | cut -f 2`
	otitle=`cat ${hmeta} | grep "^${line}" | cut -f 3`
	echo "${title}	${otitle}" >> hoppin.aaa
done < ./aaa
