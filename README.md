# Rak
Rak is a data Storage Library for Android (NoSQL) using Kryo. I was inpirated by the dish-rack (dish structure are neatly arranged).

[![](https://jitpack.io/v/isfaaghyth/Rak.svg)](https://jitpack.io/#isfaaghyth/Rak)

## Features
- [x] Insert, Remove, and Grab.
- [x] fast reading.

### Add Dependency
Add it in your root build.gradle at the end of repositories:
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
and in dependency project
```java
compile 'com.github.isfaaghyth:Rak:1.0.0'
```
### Starting
```java
Rak.initialize(context);
```
### Insert an awesome data
you can insert of any type of data for Rak value.
```java
Rak.entry("key", "value");
Rak.entry("key", hashMap);
Rak.entry("key", list);
```
### Grab it!
```java
String hai = Rak.grab("key");
HashMap<> testMap = Rak.grab("key", new HashMap());
List<> testMap = Rak.grab("key", new List());
```
### Remove
```java
Rak.remove("key"); //by key
Rak.removeAll(); //completely remove all data
```
