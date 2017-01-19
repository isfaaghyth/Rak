[![jitpack.io](https://jitpack.io/v/isfaaghyth/Rak.svg)](https://jitpack.io/#isfaaghyth/Rak)

# Rak
Rak is a data Storage Library for Android (NoSQL) using Kryo. I was inpirated by Paper-Lib mechanism and also the dish-rack (dish structure are neatly arranged).

### Features
- [x] Insert, Remove, and Grab.
- [x] fast reading.
- [x] support: POJO.

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
you must be initialize of Rak on onCreate() in Activity or Application.
```java
Rak.initialize(context);
```
### Insert an awesome data
insert data object.
```java
Rak.entry("key", "value");
Rak.entry("key", hashMap);
Rak.entry("key", list);
```
### Grab it!
grab data object.
```java
String hai = Rak.grab("key");
HashMap<> testMap = Rak.grab("key");
List<> testMap = Rak.grab("key");
```
or using default value if doesn't exist in data storage.
```java
String hai = Rak.grab("key", "default value");
HashMap<> testMap = Rak.grab("key", new HashMap());
List<> testMap = Rak.grab("key", new List());
```
### Remove
```java
Rak.remove("key"); //by key
Rak.removeAll(); //completely remove all data
```



[LICENSE](https://github.com/isfaaghyth/Rak/blob/master/LICENSE)
