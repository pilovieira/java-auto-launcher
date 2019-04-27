# updater4j

Downloader and Launcher for Java 8 Projects (or anything you want to download and run)

This auto-updater use sha-512 checksums for file comparison.

## Getting Started

If you are going to use updater4j without a dependency manager,
you must to download the 'updater4j-core.jar' jar file of the latest version
and add it to your libraries folder.

If you use maven, you must to add this dependency in your launcher project or module:

```
...
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
...
<dependencies>
  ...
  <dependency>
    <groupId>com.github.pilovieira.updater4j</groupId>
    <artifactId>updater4j-core</artifactId>
    <version>latest</version>
  </dependency>
  ...
</dependencies>
```

### Installing

##### Create a launcher and put options

You must to create a module or project that calls the Updater4j class.
In the Updater4j class you must specify the settings of your launcher:

Required configs:
- **remoteRepositoryUrl** (The artifact repository for your system. This repository must be public and must contain all the artifacts of your system, it should also contain file "loader-checksum.txt" which is generated using a plugin described below)
- **downloadPath** (The path where the system files will be downloaded)
- **launchCommand** (The command that will be executed after the artifacts are downloaded)

Optional configs:
- **lang** (The language of updater4j - Default: English)
- **message** (Message that appears when downloads are running - Default: "Updating...")
- **logo** (The inputstream of the image that appears when downloads are running - Default: "Orange download image" - You can put null here for no image)
- **canUpdateNow** (Confirmation for update. This supplier runs every time when the updater4j is started. Implement when your system needs initial verification for updates. - Default: return true)
- **launchWhenCannotUpdate** (Supplier to execute the launch command even when the validation on "canUpdateNow" fails. - Default: return false)
- **launchWhenFail** (Supplier to execute the launch command even when the update session fails. Note: When the upgrade fails, a rollback of the artifacts will be executed, returning to the original state. - Default: return false)
- **afterUpdateCallback** (Runnable that will run after the updater has successfully completed. - Default: empty runnable)

##### Updater4j example

```
public static void main(String[] args) {
  private String userHome = System.getProperty("user.home") + File.separator;
  private String systemPath = userHome + ".mySystem" + File.separator;

  new Updater4j()
    .setRemoteRepositoryUrl("http://mywebsite.com/mysystem")
    .setDownloadPath(systemPath)
    .setLaunchCommand("javaw", "-jar", systemPath + "myApp.jar")
    .start();
}
```

Now you can generate a updater jar file to update and run your application! 

## Deployment

You will must to generate a deploy folder with your application artifacts
(these are the files that the updater4j will download at startup).  

After including in deploy folder all the files needed to run the app,
you must to generate the checksum file for the updater4j can make startup comparisons.

You can generate checksum of two ways:

- Run directly from main Class of updater4j-core (with path argument) 

```
br.com.pilovieira.updater4j.checksum.ChecksumFileGenerator
```

- Attaching in the maven build lifecycle (with path parameter)

```
<pluginRepositories>
  <pluginRepository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </pluginRepository>
</pluginRepositories>
...
<build>
  <plugins>
    ...
    <plugin>
      <groupId>com.github.pilovieira.updater4j</groupId>
      <artifactId>checksum-maven-plugin</artifactId>
      <version>latest</version>
      <executions>
        <execution>
          <phase>install</phase>
          <goals>
            <goal>generate-checksum</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <path>target/</path>
      </configuration>
    </plugin>
    ...
  </plugins>
</build>
```

This step will generate a file "loader-checksum.txt" in the deploy folder.

Now you must deploy the folder content in your remote repository
(the same repository of launcher option "remoteRepositoryUrl"). 

##### Important:

**You must to generate a new checksum file whenever you make changes in any file of your project!**

**I strongly recommend adding this step to the build lifecycle!**

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/pilovieira/updater4j/tags). 

## Authors

* **Pilo Vieira** - *Initial work* - [GitHub](https://github.com/pilovieira) - [Website](http://pilovieira.com.br)

See also the list of [contributors](https://github.com/pilovieira/updater4j/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* This project was born from the must to distribute a java system without the use of the self-contained model and with the possibility of updating the clients without having to download a new full-installer.
