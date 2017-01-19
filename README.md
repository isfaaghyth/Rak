# Rak
Data Storage Library for Android.

[![](https://jitpack.io/v/isfaaghyth/Rak.svg)](https://jitpack.io/#isfaaghyth/Rak)

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
```
compile 'com.github.isfaaghyth:Rak:1.0.0'
```
### Started
```java
Rak.initialize(context);
```
### Insert awesome data
you can insert of any type of data for Rak value.
```java
Rak.entry("key", "value");
```
