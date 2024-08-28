#!/bin/bash

if [ "$2" == "" ]; then
    	echo usage: $0 \<Branch\> \<RState\>
    	exit -1
else
	versionProperties=install/version.properties
	theDate=\#$(date +"%c")
	module=$1
	branch=$2
	workspace=$3
fi

function getProductNumber {
        product=`cat ${WORKSPACE}/workspace/TPSyntaxCheck_Release//build.cfg | grep $module | grep $branch | awk -F " " '{print $3}'`
}


function setRstate {

        revision=`cat ${WORKSPACE}/workspace/TPSyntaxCheck_Release//build.cfg | grep $module | grep $branch | awk -F " " '{print $4}'`
 
       	if git tag | grep $product-$revision; then
	        rstate=`git tag | grep $revision | tail -1 | sed s/.*-// | perl -nle 'sub nxt{$_=shift;$l=length$_;sprintf"%0${l}d",++$_}print $1.nxt($2) if/^(.*?)(\d+$)/';`
        else
                ammendment_level=01
                rstate=$revision$ammendment_level
        fi
	echo "Building R-State:$rstate"

}


function appendRStateToPlatformReleaseXml {

		versionXml="${WORKSPACE}/workspace/TPSyntaxCheck_Release//src/resources/version/release.xml"
		
		if [ ! -e ${versionXml} ] ; then
			echo "version xml file is missing from build: ${versionXml}"
			exit -1
		fi

		mv ${WORKSPACE}/workspace/TPSyntaxCheck_Release//src/resources/version/release.xml ${WORKSPACE}/workspace/TPSyntaxCheck_Release//src/resources/version/release.${rstate}.xml

}

getProductNumber
setRstate
git checkout $branch
git pull origin $branch
appendRStateToPlatformReleaseXml

#add maven command here
/proj/eiffel004_config/fem156/slaves/RHEL_ENIQ_STATS/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.0.5/bin/mvn exec:exec

rsp=$?

if [ $rsp == 0 ]; then

  git tag $product-$rstate
  git pull
  git push --tag origin $branch

fi

exit $rsp
