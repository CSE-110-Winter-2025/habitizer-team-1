plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // This library contains no executable code, so it is
    // safe to use even in a non-Android library module.
    implementation(libs.androidx.annotations)
    implementation(libs.filament.android)
    implementation(libs.impress)
    implementation(libs.firebase.database)

    testImplementation(libs.junit4)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.core)
}