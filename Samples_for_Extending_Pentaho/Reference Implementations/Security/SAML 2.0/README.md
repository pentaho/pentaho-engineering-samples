# pentaho-saml-sample #
Pentaho SAML Authentication Sample

#### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 11
* This [settings.xml](https://bintray.com/pentaho/public-maven/download_file?file_path=settings.xml) in your <user-home>/.m2 directory
	

#### Building it

At the root of the project, type

```
$ mvn clean package
```


This will build, unit test, and package the whole project (all of the sub-modules). The resulting pentaho-saml-sample-<BUILD-VERSION>
artifact will be generated in: ```./pentaho-saml-assembly/target```
