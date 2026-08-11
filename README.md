# Webys

**Webys** is a simple Java browser launcher by **Jergan Studio**.

## Run Webys

Download `Webys.jar` from the latest GitHub release, then run:

```powershell
java -jar Webys.jar
```

You need Java installed on your computer.

## Build manually

```powershell
javac Webys.java
jar cfe Webys.jar Webys Webys.class
java -jar Webys.jar
```

The GitHub Actions workflow builds `Webys.jar` automatically for releases.
