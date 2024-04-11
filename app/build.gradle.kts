plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vdsl.cybermart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vdsl.cybermart"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    viewBinding {
        enable = true
    }
    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.airbnb.android:lottie:5.2.0")

    implementation("com.google.android.material:material:1.11.0")
    implementation("com.saadahmedev.popup-dialog:popup-dialog:1.0.5")

    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.nex3z:notification-badge:1.0.4")

    implementation("com.google.firebase:firebase-storage")
    implementation("com.sendgrid:sendgrid-java:4.10.2")
    implementation ("org.apache.httpcomponents:httpclient:4.5.14")
    implementation ("org.apache.httpcomponents:httpcore:4.4.16")

}