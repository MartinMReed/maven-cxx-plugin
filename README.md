# Usage
A [Maven](http://maven.apache.org/download.html) plugin with support for gcc, g++, ar, arduino, xcode, Adobe Air/Flex, BlackBerry PlayBook and BB10.

# Build or Download
To build this you need to use [Maven](http://maven.apache.org/download.html) with the [hbc-maven-core](https://github.com/hardisonbrewing/hbc-maven-core) project. Alternatively you can pull the latest version of hbc-maven-core from [http://repo.hardisonbrewing.org](http://repo.hardisonbrewing.org) (see repository settings below).

# Pulling the latest version from Nexus
To pull the latest version of the plugin you will need to update your [remote repository](http://maven.apache.org/guides/introduction/introduction-to-repositories.html) settings under your `.m2/settings.xml`.

```xml
<repositories>
	<repository>
		<id>hardisonbrewing-releases</id>
		<name>hardisonbrewing-releases</name>
		<url>http://repo.hardisonbrewing.org/content/repositories/releases/</url>
	</repository>
	<repository>
		<id>hardisonbrewing-snapshots</id>
		<name>hardisonbrewing-snapshots</name>
		<url>http://repo.hardisonbrewing.org/content/repositories/snapshots/</url>
	</repository>
</repositories>
```

To download this plugin without building it manually, you can add the following remote plugin repository:

```xml
<pluginRepositories>
	<pluginRepository>
		<id>hardisonbrewing-releases</id>
		<name>hardisonbrewing-releases</name>
		<url>http://repo.hardisonbrewing.org/content/repositories/releases/</url>
	</pluginRepository>
	<pluginRepository>
		<id>hardisonbrewing-snapshots</id>
		<name>hardisonbrewing-snapshots</name>
		<url>http://repo.hardisonbrewing.org/content/repositories/snapshots/</url>
	</pluginRepository>
</pluginRepositories>
```

Continuous Integration: [Bamboo Status](http://bamboo.hardisonbrewing.org/browse/MVN-CXX)

# Setting up your pom.xml
Packages that are <del>struck out</del> have been disabled, but are still present in the source code.

## Specify the packaging
The `<packaging/>` in your `pom.xml` must be set to package classifier for the type of tool you're project should build for. See supported tools and package types below:

<table>
<thead><th>Packaging</th><th>Target</th></thead>
<tr><td>a</td><td>ar</td></tr>
<tr><td>o</td><td>gcc/g++</td></tr>
<tr><td><del>as.bar</del></td><td><del>BlackBerry 10, ActionScript main file</del></td></tr>
<tr><td><del>mxml.bar</del></td><td><del>BlackBerry 10, MXML main file</del></td></tr>
<tr><td><del>js.bar</del></td><td><del>BlackBerry 10, WebWorks</del></td></tr>
<tr><td>cdt</td><td>Eclipse CDT, BlackBerry 10 QDE</td></tr>
<tr><td>qnx</td><td>BlackBerry 10, Makefile</td></tr>
<tr><td><del>arduino</del></td><td><del>Arduino</del></td></tr>
<tr><td>xcode</td><td>xcodebuild</td></tr>
<tr><td>flex</td><td>Adobe Air/Flex</td></tr>
</table>

## Configuring the plugin
Under the plugin `<configuration/>` you may be able to specify additional settings depending on the selected packaging:

<table>
<thead><th>Packaging</th><th>Configurations</th></thead>
<tr><td>a</td><td>language<br/>
sources</td></tr>
<tr><td>o</td><td>language<br/>
sources<br/>
libs<br/>
frameworks</td></tr>
<tr><td><del>*.bar</del></td><td/></tr>
<tr><td>cdt</td><td>target (Release, Release-Device, etc)</td></tr>
<tr><td>qnx</td><td>target (Application w/ bar-descriptor.xml)</td></tr>
<tr><td><del>arduino</del></td><td><del>sketchbook<br/>
targetDevice<br/>
sources</del></td></tr>
<tr><td>xcode</td><td>configuration (Debug/Release/etc)<br/>
scheme<br/>
targetIncludes<br/>
targetExcludes (Usually test targets)<br/>
provisioningProfile<br/>
codesignCertificate<br/>
keychain<br/>
keychain/keychain<br/>
keychain/password</td></tr>
<tr><td>flex</td><td>target (air, apk, ipa-app-store, <a href="http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html">etc</a>)<br/>
sourceFile (i.e. src/Main.mxml)<br/>
libDirectory (Directory containing SWC libs)<br/>
descriptorFile (Application descriptor XML)<br/>
provisioningProfile (iOS provisioning profile)<br/>
keystore<br/>
keystore/keystore<br/>
keystore/storepass<br/>
keystore/keypass<br/>
keystore/alias</td></tr>
</table>

## Properties
Usable through the `settings.xml`, `pom.xml` or commandline with a `-Dkey=value`

<table>
<thead><th>Packaging</th><th>Properties</th></thead>
<tr><td>a</td><td/></tr>
<tr><td>o</td><td/></tr>
<tr><td><del>*.bar</del></td><td><del>adobe.flex.home<br/>
blackberry.webworks.tablet.home<br/>
blackberry.tablet.home<br/>
blackberry.tablet.device.ip<br/>
blackberry.tablet.device.password<br/>
debug</del></td></tr>
<tr><td>cdt</td><td/></tr>
<tr><td>qnx</td><td/></tr>
<tr><td><del>arduino</del></td><td><del>arduino.home<br/>
avr.bin<br/>
avrdude.config.path<br/>
serial.port</del></td></tr>
<tr><td>xcode</td><td/></tr>
<tr><td>flex</td><td>adobe.flex.home</td></tr>
</table>

# Sample: C++ Library POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <parent>
  <groupId>org.hardisonbrewing</groupId>
    <artifactId>commons-c-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
  </parent>
  <groupId>org.hardisonbrewing</groupId>
  <artifactId>libhbc_math</artifactId>
  <name>${project.artifactId}</name>
  <packaging>a</packaging>
  <build>
    <!-- <sourceDirectory/> not required -->
    <sourceDirectory>src</sourceDirectory/> 
    <resources>
     <resource>
      <directory>src</directory>
        <includes>
          <include>**/*.h</include>
        </includes>
     </resource>
     <resource>
      <directory>third-party</directory>
        <includes>
          <include>**/*.h</include>
        </includes>
     </resource> 
    </resources>
    <plugins>
     <plugin>
      <groupId>org.hardisonbrewing</groupId>
      <artifactId>maven-cxx-plugin</artifactId>
      <extensions>true</extensions>
      <configuration>
        <!-- language of c++ will delegate to g++ instead of gcc -->
        <language>c++</language>
        <sources>
          <source>
            <!-- no <directory/> means use <sourceDirectory/> -->
            <includes>
              <include>**/*.cc</include>
            </includes>
<!--             <excludes>-->
<!--                <exclude>**/*.m</exclude>-->
<!--             </excludes>-->
          </source> 
          <source>
            <directory>third-party</directory>
<!--             <includes>-->
<!--              <include>**/*.cc</include>-->
<!--             </includes>-->
            <excludes>
              <exclude>**/*.m</exclude>
            </excludes>
          </source>
        </sources>
      </configuration>
     </plugin>
    </plugins>
  </build>
</project>
```

# Sample: XCode Project POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>net.hardisonbrewing</groupId>
  <artifactId>komodododo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>${project.artifactId}</name>
  <packaging>xcode</packaging>
  <profiles>
    <profile>
      <id>ios-distribution</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.hardisonbrewing</groupId>
            <artifactId>maven-cxx-plugin</artifactId>
            <extensions>true</extensions>
            <configuration>
              <provisioningProfile>Komodododo_AdHoc_Dist.mobileprovision</provisioningProfile>
              <codesignCertificate>distribution_identity.cer</codesignCertificate>
            </configuration>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
  <build>
    <plugins>
      <plugin>
        <groupId>org.hardisonbrewing</groupId>
        <artifactId>maven-cxx-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <provisioningProfile>Komodododo_AdHoc.mobileprovision</provisioningProfile>
          <codesignCertificate>developer_identity.cer</codesignCertificate>
          <keychain>
            <keychain>/tools/keychains/ios.keychain</keychain>
            <password>password</password>
          </keychain>
          <targetExcludes>
            <targetExclude>KomodododoTests</targetExclude>
          </targetExcludes>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

# Sample: Adobe Air/Flex Project POM

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>net.hardisonbrewing</groupId>
  <artifactId>komodododo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>${project.artifactId}</name>
  <packaging>flex</packaging>
  <properties>
    <adobe.flex.home>/tools/adobe/flex_sdk_4.5</adobe.flex.home>
  </properties>
  <build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
      <plugin>
        <groupId>org.hardisonbrewing</groupId>
        <artifactId>maven-cxx-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <target>air</target>
          <!--<target>ipa-app-store</target>-->
          <sourceFile>Main.mxml</sourceFile>
          <libDirectory>libs</libDirectory>
          <descriptorFile>src/Main-app.xml</descriptorFile>
          <!--<provisioningProfile>komodododo.mobileprovision</provisioningProfile>-->
          <keystore>
            <keystore>keystore.p12</keystore>
            <storepass>storepass</storepass>
            <!--<keypass>keypass</keypass>-->
            <!--<alias>alias</alias>-->
          </keystore>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

# License
GNU Lesser General Public License, Version 3.0.
