#!/bin/sh
/*! ******************************************************************************
 *
 * Pentaho Engineering Samples
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

echo "************************************************************"
echo " Installing Oracle Java 8"
echo "************************************************************"

JAVA_BASE_VERSION="8"
JAVA_SUB_VERSION="151"
JAVA_BASE_BUILD="12"

JAVA_VERSION="${JAVA_BASE_VERSION}u${JAVA_SUB_VERSION}"
JAVA_BUILD="b${JAVA_BASE_BUILD}"
JAVA_VERSION_WITH_BUILD="${JAVA_VERSION}-${JAVA_BUILD}"

sudo wget --no-cookies --header "Cookie: gpw_e24=xxx; oraclelicense=accept-securebackup-cookie;" "http://download.oracle.com/otn-pub/java/jdk/${JAVA_VERSION_WITH_BUILD}/e758a0de34e24606bca991d704f6dcbf/jdk-${JAVA_VERSION}-linux-x64.rpm"

sudo rpm -i jdk-${JAVA_VERSION}-linux-x64.rpm

sudo /usr/sbin/alternatives --install /usr/bin/java java /usr/java/jdk1.${JAVA_BASE_VERSION}.0_${JAVA_SUB_VERSION}/bin/java 20000

sudo echo "export JAVA_HOME=/usr/java/default" | sudo tee -a /etc/profile.d/set-emr-env.sh
sudo echo "export JAVA_HOME=/usr/java/default" | sudo tee -a /home/hadoop/.bashrc