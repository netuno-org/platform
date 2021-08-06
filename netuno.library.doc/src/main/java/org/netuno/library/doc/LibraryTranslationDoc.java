package org.netuno.library.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LibraryTranslationDoc {
    LanguageDoc language();
    String title();
    String introduction();
    SourceCodeDoc[] howToUse();
}
