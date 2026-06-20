# Hilt
-keepattributes *Annotation*
-dontwarn dagger.hilt.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Protobuf
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }
