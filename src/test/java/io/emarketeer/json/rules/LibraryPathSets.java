package io.emarketeer.json.rules;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LibraryPathSets {
    String[] value() default {};
}
