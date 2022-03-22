-injars       out/artifacts/netuno-proteu.jar;out/artifacts/netuno-tritao.jar
-outjars      out/proguard/netuno-web.jar(org/netuno/**)

-adaptresourcefilecontents META-INF/MANIFEST.MF

#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers

# JDK 8
#-libraryjars  <java.home>/lib/rt.jar;out/artifacts/netuno-library-doc.jar;out/artifacts/netuno-psamata.jar
# JDK 11
-libraryjars  <java.home>/jmods;out/artifacts/netuno-library-doc.jar;out/artifacts/netuno-psamata.jar
#-libraryjars ../../netuno-proteu/target/lib:../target/lib
#-libraryjars ../../netuno-proteu/target/lib:../target/lib

#-dontpreverify
#-dontoptimize

#-allowaccessmodification
-dontnote **
-dontwarn **
-ignorewarnings

-printmapping out.map

#-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class io.netty.** { *; }
-dontwarn io.netty.**

-keep class oshi.** { *; }
-dontwarn oshi.**

-keep class com.** { *; }
-dontwarn com.**

-keep class org.json.** { *; }
-dontwarn org.json.**

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keep interface * { *; }

-keep public class * {
    public protected private *;
}

-keep class * {
    public protected private *;
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