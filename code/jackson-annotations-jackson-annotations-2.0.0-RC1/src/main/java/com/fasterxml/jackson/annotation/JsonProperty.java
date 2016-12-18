package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static
 * method as a "setter" or "getter" for a logical property
 * (depending on its signature),
 * or non-static object field to be used (serialized, deserialized) as
 * a logical property.
 *<p>
 * Default value ("") indicates that the field name is used
 * as the property name without any modifications, but it
 * can be specified to non-empty value to specify different
 * name. Property name refers to name used externally, as
 * the field name in JSON objects.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonProperty
{
    /**
     * Defines name of the logical property, i.e. Json object field
     * name to use for the property: if empty String (which is the
     * default), will use name of the field that is annotated.
     */
    String value() default "";

    /**
     * Property that indicates whether a value (which may be explicit
     * null) is expected for property during deserialization or not.
     * If expected, <code>BeanDeserialized</code> should indicate
     * this as a validity problem (usually by throwing an exception,
     * but this may be sent via problem handlers that can try to
     * rectify the problem, for example, by supplying a default
     * value).
     *<p>
     * Note that as of 2.0, this property is NOT used by
     * <code>BeanDeserializer</code>: support is expected to be
     * added for later minor versions.
     * 
     * @since 2.0
     */
    boolean required() default false;

    /* NOTE: considering of adding ability to specify default
     * String value -- would work well for scalar types, most of
     * which can coerce from Strings. But won't add for 2.0 yet.
     */
    //String defaultValue() default "";
}
