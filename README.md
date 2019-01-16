# updater4j

Downloader and Launcher for Java 8 Projects (or anything you want to download and run)

## Getting Started

If you are going to use updater4j without a dependency manager,
you need to download the 'updater4j-core.jar' jar file of the latest version
and add it to your libraries folder.

If you use maven, you need to add this dependency in your project:

```
<dependencies>
  ...
  <dependency>
    <groupId>br.com.pilovieira.updater4j</groupId>
    <artifactId>updater4j-core</artifactId>
    <version>latest</version>
  </dependency>
  ...
</dependencies>
```

### Installing

##### Create a launcher and put options

- implement the launcher with examples (TODO)

```
Give examples
```

## Deployment

You will need to generate a deploy folder with your application artifacts
(these are the files that the updater4j will download at startup).  

After including in deploy folder all the files needed to run the app,
you need to generate the checksum file for the updater4j can make startup comparisons.

You can generate checksum of two ways:

- Run directly from main Class of updater4j-core (with path argument) 

```
br.com.pilovieira.updater4j.checksum.ChecksumFileGenerator
```

- Attaching in the maven build lifecycle (with path parameter)

```
<build>
  <plugins>
    ...
    <plugin>
      <groupId>br.com.pilovieira.updater4j</groupId>
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

Now you need deploy the folder content in your remote repository
(the same repository of launcher option "remoteRepositoryUrl"). 

##### Important:

**You need to generate a new checksum file whenever you make changes in any file of your project!**

**I strongly recommend adding this step to the build lifecycle!**

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/pilovieira/updater4j/tags). 

## Authors

* **Pilo Vieira** - *Initial work* - [GitHub](https://github.com/pilovieira) - [Website](http://pilovieira.com.br)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* This project was born from the need to distribute a java system without the use of the self-contained model and with the possibility of updating the clients without having to download a new full-installer.
