Usage Notes
=====
For detailed usage notes please visit: [http://hardisonbrewing.org/x/EIAT](http://hardisonbrewing.org/x/EIAT)

Build or Download
-----------------
To build this you need to use Maven with the [hbc-maven-core](https://github.com/hardisonbrewing/hbc-maven-core) project.

OR add the following remote repository to download the latest snapshot:

	<repositories>
		<repository>
			<id>hardisonbrewing-releases</id>
			<name>hardisonbrewing-releases</name>
			<url>http://repo.hardisonbrewing.org/nexus/content/repositories/releases/</url>
		</repository>
		<repository>
			<id>hardisonbrewing-snapshots</id>
			<name>hardisonbrewing-snapshots</name>
			<url>http://repo.hardisonbrewing.org/nexus/content/repositories/snapshots/</url>
		</repository>
	</repositories>

To download this plugin without building it manually, you can add the following remote plugin repository:

	<pluginRepositories>
		<pluginRepository>
			<id>hardisonbrewing-releases</id>
			<name>hardisonbrewing-releases</name>
			<url>http://repo.hardisonbrewing.org/nexus/content/repositories/releases/</url>
		</pluginRepository>
		<pluginRepository>
			<id>hardisonbrewing-snapshots</id>
			<name>hardisonbrewing-snapshots</name>
			<url>http://repo.hardisonbrewing.org/nexus/content/repositories/snapshots/</url>
		</pluginRepository>
	</pluginRepositories>