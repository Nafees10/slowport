# SlowPort

SlowPort is a Desktop Application for optimization of timetables.

It was made for use by FAST NUCES students during registration period, to help
them find the best timetable according to their needs.


## Building

You need the following tools to build:

* git
* gradle
* JDK (Java 17 minimum)

Run the following commands one after another:

```bash
git clone https://github.com/Nafees10/slowport
cd slowport
gradle wrapper
./gradlew build
./gradlew shadowJar
```

Running above will fetch all the required dependencies and build the JAR file.
