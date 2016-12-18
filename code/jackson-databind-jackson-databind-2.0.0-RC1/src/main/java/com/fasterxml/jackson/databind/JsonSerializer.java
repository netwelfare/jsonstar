package com.fasterxml.jackson.databind;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * Abstract class that defines API used by {@link ObjectMapper} (and
 * other chained {@link JsonSerializer}s too) to serialize Objects of
 * arbitrary types into JSON, using provided {@link JsonGenerator}.
 *<p>
 * NOTE: it is recommended that custom serializers extend
 * {@link com.fasterxml.jackson.databind.ser.std.StdSerializer} instead
 * of this class, since it will implement many of optional
 * methods of this class.
 *<p>
 * If serializer is an aggregate one -- meaning it delegates handling of some
 * of its contents by using other serializer(s) -- it typically also needs
 * to implement {@link com.fasterxml.jackson.databind.ser.ResolvableSerializer},
 * which can locate secondary serializers needed. This is important to allow dynamic
 * overrides of serializers; separate call interface is needed to separate
 * resolution of secondary serializers (which may have cyclic link back
 * to serializer itself, directly or indirectly).
 *<p>
 * In addition, to support per-property annotations (to configure aspects
 * of serialization on per-property basis), serializers may want
 * to implement 
 * {@link com.fasterxml.jackson.databind.ser.ContextualSerializer},
 * which allows specialization of serializers: call to
 * {@link com.fasterxml.jackson.databind.ser.ContextualSerializer#createContextual}
 * is passed information on property, and can create a newly configured
 * serializer for handling that particular property.
 *<p>
 * If both
 * {@link com.fasterxml.jackson.databind.ser.ResolvableSerializer} and
 * {@link com.fasterxml.jackson.databind.ser.ContextualSerializer}
 * are implemented, resolution of serializers occurs before
 * contextualization.
 */
public abstract class JsonSerializer<T>
{
    /*
    /**********************************************************
    /* Fluent factory methods for constructing decorated versions
    /**********************************************************
     */

    /**
     * Method that will return serializer instance that produces
     * "unwrapped" serialization, if applicable for type being
     * serialized (which is the case for some serializers
     * that produce JSON Objects as output).
     * If no unwrapped serializer can be constructed, will simply
     * return serializer as-is.
     *<p>
     * Default implementation just returns serializer as-is,
     * indicating that no unwrapped variant exists
     * 
     * @param unwrapper Name transformation to use to convert between names
     *   of unwrapper properties
     */
    public JsonSerializer<T> unwrappingSerializer(NameTransformer unwrapper) {
        return this;
    }

    /**
     * Accessor for checking whether this serializer is an
     * "unwrapping" serializer; this is necessary to know since
     * it may also require caller to suppress writing of the
     * leading property name.
     */
    public boolean isUnwrappingSerializer() {
        return false;
    }
    
    /*
    /**********************************************************
    /* Serialization methods
    /**********************************************************
     */

    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value Value to serialize; can <b>not</b> be null.
     * @param jgen Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     *   serializing Objects value contains, if any.
     */
    public abstract void serialize(T value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException;

    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles, using specified type serializer
     * for embedding necessary type information.
     *<p>
     * Default implementation will ignore serialization of type information,
     * and just calls {@link #serialize}: serializers that can embed
     * type information should override this to implement actual handling.
     * Most common such handling is done by something like:
     *<pre>
     *  // note: method to call depends on whether this type is serialized as JSON scalar, object or Array!
     *  typeSer.writeTypePrefixForScalar(value, jgen);
     *  serialize(value, jgen, provider);
     *  typeSer.writeTypeSuffixForScalar(value, jgen);
     *</pre>
     *
     * @param value Value to serialize; can <b>not</b> be null.
     * @param jgen Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     *   serializing Objects value contains, if any.
     * @param typeSer Type serializer to use for including type information
     */
    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException, JsonProcessingException
    {
        serialize(value, jgen, provider);
    }
    
    /*
    /**********************************************************
    /* Introspection methods needed for type handling 
    /**********************************************************
     */
    
    /**
     * Method for accessing type of Objects this serializer can handle.
     * Note that this information is not guaranteed to be exact -- it
     * may be a more generic (super-type) -- but it should not be
     * incorrect (return a non-related type).
     *<p>
     * Default implementation will return null, which essentially means
     * same as returning <code>Object.class</code> would; that is, that
     * nothing is known about handled type.
     *<p>
     */
    public Class<T> handledType() { return null; }

    /**
     * Method called to check whether given serializable value is
     * considered "empty" value (for purposes of suppressing serialization
     * of empty values).
     *<p>
     * Default implementation will consider only null values to be empty.
     * 
     * @since 2.0
     */
    public boolean isEmpty(T value) {
        return (value == null);
    }

    /**
     * Method that can be called to see whether this serializer instance
     * will use Object Id to handle cyclic references.
     */
    public boolean usesObjectId() {
        return false;
    }
    
    /*
    /**********************************************************
    /* Helper class(es)
    /**********************************************************
     */

    /**
     * This marker class is only to be used with annotations, to
     * indicate that <b>no serializer is configured</b>.
     *<p>
     * Specifically, this class is to be used as the marker for
     * annotation {@link com.fasterxml.jackson.databind.annotation.JsonSerialize}.
     */
    public abstract static class None
        extends JsonSerializer<Object> { }
}
