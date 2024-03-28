
-injars       out/artifacts/netuno-cli.jar
-outjars      out/proguard/netuno.jar

# JDK 8
#-libraryjars  <java.home>/lib/rt.jar:<java.home>/lib/jce.jar:<java.home>/../lib/tools.jar
# JDK 11
-libraryjars  <java.home>/jmods

-adaptresourcefilenames    **.properties,**.gif,**.jpg,**.png,**.svg,**.xml,**.html,**.js,**.json
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

#-dontpreverify
#-dontoptimize

-dontnote **
-dontwarn **
-ignorewarnings

-printmapping out.map

#-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,MethodParameters,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep interface ** { *; }

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keep class org.eclipse.** { *; }
-dontwarn org.eclipse.**

-keep class org.json.** { *; }
-dontwarn org.json.**

-keep class org.fusesource.** { *; }
-dontwarn org.fusesource.**

-keep class picocli.** { *; }
-dontwarn picocli.**

-keep class org.javadelight.** { *; }
-dontwarn org.javadelight.**

-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

-keep class org.h2.** { *; }
-dontwarn org.h2.**

-keep class com.mchange.** { *; }
-dontwarn com.mchange.**

-keep class org.quartz.** { *; }
-dontwarn org.quartz.**

-keep class com.sun.** { *; }
-dontwarn com.sun.**

-keep class jdk.** { *; }
-dontwarn jdk.**

-keep class org.graalvm.** { *; }
-dontwarn org.graalvm.**

-keep class com.oracle.** { *; }
-dontwarn com.oracle.**

-keep class !org.netuno.** { *; }
-dontwarn !org.netuno.**


-dontwarn module-info

-keep public class * {
    public protected *;
}

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#-keep class com.xxx.A { *; }
#-keep class com.xxx.A$B { *; }
#-keep class com.xxx.A$C { *; }

