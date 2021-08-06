package org.netuno.library.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDoc {
    String dependency() default "";
    MethodTranslationDoc[] translations();
    ParameterDoc[] parameters();
    ReturnTranslationDoc[] returns();
}
