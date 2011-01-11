Usage Notes
=====
For detailed usage notes please visit: http://hardisonbrewing.org/x/EIAT

Build or Download
-----------------
To build this you need to use Maven with the [hbc-maven-core](https://github.com/hardisonbrewing/hbc-maven-core) project.

OR add the following remote repository to download the latest snapshot:

	<repositories>
	   <repository>
	      <id>hardisonbrewing-public</id>
	      <name>hardisonbrewing-public</name>
	      <url>http://repo.hardisonbrewing.org/nexus/content/groups/public</url>
	   </repository>
	</repositories>

To download this plugin without building it manually, you can add the following remote plugin repository:

	<pluginRepositories>
	   <pluginRepository>
	      <id>hardisonbrewing-public</id>
	      <name>hardisonbrewing-public</name>
	      <url>http://repo.hardisonbrewing.org/nexus/content/groups/public</url>
	   </pluginRepository>
	</pluginRepositories>

TODO Notes
----------
* I think I forgot to handle additional resources besides the icon... I need to look into that.
* I'll get a JIRA instance up eventually for tracking these TODO items.