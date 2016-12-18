package com.fasterxml.jackson.databind.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * Deserializer only used for abstract types used as placeholders during polymorphic
 * type handling deserialization. If so, there is no real deserializer associated
 * with nominal type, just {@link TypeDeserializer}; and any calls that do not
 * pass such resolver will result in an error.
 * 
 * @author tatu
 */
public class AbstractDeserializer
    extends JsonDeserializer<Object>
{
    protected final JavaType _baseType;

    // support for "native" types, which require special care:
    
    protected final boolean _acceptString;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptInt;
    protected final boolean _acceptDouble;
    
    public AbstractDeserializer(JavaType bt)
    {
        _baseType = bt;
        Class<?> cls = bt.getRawClass();
        _acceptString = cls.isAssignableFrom(String.class);
        _acceptBoolean = (cls == Boolean.TYPE) || cls.isAssignableFrom(Boolean.class);
        _acceptInt = (cls == Integer.TYPE) || cls.isAssignableFrom(Integer.class);
        _acceptDouble = (cls == Double.TYPE) || cls.isAssignableFrom(Double.class);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        // First: support "natural" values (which are always serialized without type info!)
        Object result = _deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        // This method should never be called...
        throw ctxt.instantiationException(_baseType.getRawClass(),
                "abstract types either need to be mapped to concrete types, have custom deserializer, or be instantiated with additional type information");
    }

    protected Object _deserializeIfNatural(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        /* As per [JACKSON-417], there is a chance we might be "natural" types
         * (String, Boolean, Integer, Double), which do not include any type information...
         * Care must be taken to only return this if return type matches, however.
         * Finally, we may have to consider possibility of custom handlers for
         * these values: but for now this should work ok.
         */
        switch (jp.getCurrentToken()) {
        case VALUE_STRING:
            if (_acceptString) {
                return jp.getText();
            }
            break;
        case VALUE_NUMBER_INT:
            if (_acceptInt) {
                return jp.getIntValue();
            }
            break;

        case VALUE_NUMBER_FLOAT:
            if (_acceptDouble) {
                return Double.valueOf(jp.getDoubleValue());
            }
            break;
        case VALUE_TRUE:
            if (_acceptBoolean) {
                return Boolean.TRUE;
            }
            break;
        case VALUE_FALSE:
            if (_acceptBoolean) {
                return Boolean.FALSE;
            }
            break;
        }
        return null;
    }
}

