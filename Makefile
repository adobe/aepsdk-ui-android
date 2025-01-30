checkstyle:
		(./code/gradlew -p code/notificationbuilder checkstyle)

checkformat:
		(./code/gradlew -p code/notificationbuilder spotlessCheck)

ci-lint: checkformat checkstyle

format:
		(./code/gradlew -p code/notificationbuilder spotlessApply)

format-license:
		(./code/gradlew -p code licenseFormat)

javadoc:
		(./code/gradlew -p code/notificationbuilder javadocJar)

unit-test:
		(./code/gradlew -p code/notificationbuilder testPhoneDebugUnitTest)

unit-test-coverage:
		(./code/gradlew -p code/notificationbuilder createPhoneDebugUnitTestCoverageReport)

functional-test:
		(./code/gradlew -p code/notificationbuilder uninstallPhoneDebugAndroidTest)
		(./code/gradlew -p code/notificationbuilder connectedPhoneDebugAndroidTest)

functional-test-coverage:
		(./code/gradlew -p code/notificationbuilder createPhoneDebugAndroidTestCoverageReport)

assemble-phone:
		(./code/gradlew -p code/notificationbuilder  assemblePhone)

assemble-phone-release:
		(./code/gradlew -p code/notificationbuilder assemblePhoneRelease)

assemble-app:
		(./code/gradlew -p code/testapp  assemble)

notificationbuilder-publish-maven-local-jitpack: assemble-phone-release
		(./code/gradlew -p code/notificationbuilder publishReleasePublicationToMavenLocal -Pjitpack  -x signReleasePublication)

notificationbuilder-publish-snapshot: assemble-phone-release
		(./code/gradlew -p code/notificationbuilder publishReleasePublicationToSonatypeRepository)

notificationbuilder-publish-main: assemble-phone-release
		(./code/gradlew -p code/notificationbuilder  publishReleasePublicationToSonatypeRepository -Prelease)
