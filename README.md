Turkmen dictionary for Android
==============================

An app for looking up words online.

Development requirements
------------------------

	* Gradle
	* Android Studio
	
	
Releasing
---------

Increment version in root build gradle.

Uncomment `signingConfig signingConfigs.release` in the app/build.gradle file. Copy the `enedilimkeys` keyfile to `app/`, and then run:

    $ ./gradlew assembleRelease

