#!/bin/bash
# This script generates actual dsl code in dynamic folder by useing groovy templates.
# This script executes on Jenkins during seed job build.

# reading input file, ignoring blank & commented lines and converting all input to lower case
cat app-list-input.txt | grep -v '^#' |grep -v ^$ | tr A-Z a-z |while read serviveName gitUrl
do
echo $serviveName $gitUrl

# coping template to dunamic folder with service name prefix.
cp master-code/dev-job-template.groovy dynamic-list/${serviveName}DevJob.groovy
cp master-code/qa-job-template.groovy dynamic-list/${serviveName}qaJob.groovy
cp master-code/stage-job-template.groovy dynamic-list/${serviveName}StageJob.groovy

# Replacing tokens, SERVICENAME & BITBUCKETURL with exact value as per input file.
sed -i "s/SERVICENAME/${serviveName}/" dynamic-list/${serviveName}DevJob.groovy
sed -i "s/BITBUCKETURL/${gitUrl}/" dynamic-list/${serviveName}DevJob.groovy

sed -i "s/SERVICENAME/${serviveName}/" dynamic-list/${serviveName}qaJob.groovy
sed -i "s/BITBUCKETURL/${gitUrl}/" dynamic-list/${serviveName}qaJob.groovy

sed -i "s/SERVICENAME/${serviveName}/" dynamic-list/${serviveName}StageJob.groovy
sed -i "s/BITBUCKETURL/${gitUrl}/" dynamic-list/${serviveName}StageJob.groovy

done
# to generate Jenkins view
cp master-code/viewtemplate.groovy dynamic-list/viewtemplate.groovy
