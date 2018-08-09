#!/bin/bash


naver_pair="./naver.pair"

for uid in `cat ${naver_pair} | cut -f 2 | sort | uniq -c | sort -nr | grep "^  *[2-9]" | sed 's/^  *//g' | cut -d " " -f 2`
do
	utitle=`cat ../resources/meta/tstore-meta.uni | grep "^${uid}" | cut -f 4`
	udirector=`cat ../resources/meta/tstore-meta.uni | grep "^${uid}" | cut -f 11`
	uactor=`cat ../resources/meta/tstore-meta.uni | grep "^${uid}" | cut -f 12`
	nids=`cat ${naver_pair} | grep "${uid}" | cut -f 1 | awk '{for(i=1;i<=NF;i++){printf("%s,",$0)}}' | sed 's/,$//g'`

	echo "${uid}	${nids}	${utitle}	${udirector}	${uactor}"
done
