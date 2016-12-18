package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.*;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;

public final class AnnotatedMethod
    extends AnnotatedWithParams
{
    final protected Method _method;

    // // Simple lazy-caching:

    protected Class<?>[] _paramClasses;

    /*
    /*****************************************************
    /* Life-cycle
    /*****************************************************
     */

    public AnnotatedMethod(Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations)
    {
        super(classAnn, paramAnnotations);
        if (method == null) {
        	throw new IllegalArgumentException("Can not construct AnnotatedMethod with null Method");
        }
        _method = method;
    }

    /**
     * Method that constructs a new instance with settings (annotations, parameter annotations)
     * of this instance, but with different physical {@link Method}.
     */
    public AnnotatedMethod withMethod(Method m)
    {
        return new AnnotatedMethod(m, _annotations, _paramAnnotations);
    }
    
    @Override
    public AnnotatedMethod withAnnotations(AnnotationMap ann) {
        return new AnnotatedMethod(_method, ann, _paramAnnotations);
    }

    /*
    /*****************************************************
    /* Annotated impl
    /*****************************************************
     */

    @Override
    public Method getAnnotated() { return _method; }

    @Override
    public int getModifiers() { return _method.getModifiers(); }

    @Override
    public String getName() { return _method.getName(); }

    /**
     * For methods, this returns declared return type, which is only
     * useful with getters (setters do not return anything; hence "void"
     * type is returned here)
     */
    @Override
    public Type getGenericType() {
        return _method.getGenericReturnType();
    }

    /**
     * For methods, this returns declared return type, which is only
     * useful with getters (setters do not usually return anything;
     * hence "void" type is returned here)
     */
    @Override
    public Class<?> getRawType() {
        return _method.getReturnType();
    }

    /**
     * As per [JACKSON-468], we need to also allow declaration of local
     * type bindings; mostly it will allow defining bounds.
     */
    @Override
    public JavaType getType(TypeBindings bindings) {
        return getType(bindings, _method.getTypeParameters());
    }

    @Override
    public final Object call() throws Exception {
        return _method.invoke(null);
    }

    @Override
    public final Object call(Object[] args) throws Exception {
        return _method.invoke(null, args);
    }

    @Override
    public final Object call1(Object arg) throws Exception {
        return _method.invoke(null, arg);
    }
    
    /*
    /********************************************************
    /* AnnotatedMember impl
    /********************************************************
     */

    @Override
    public Class<?> getDeclaringClass() { return _method.getDeclaringClass(); }

    @Override
    public Method getMember() { return _method; }

    @Override
    public void setValue(Object pojo, Object value)
        throws IllegalArgumentException
    {
        try {
            _method.invoke(pojo, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() with method "
                    +getFullName()+": "+e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to setValue() with method "
                    +getFullName()+": "+e.getMessage(), e);
        }
    }

    @Override
    public Object getValue(Object pojo) throws IllegalArgumentException
    {
        try {
            return _method.invoke(pojo);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to getValue() with method "
                    +getFullName()+": "+e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to getValue() with method "
                    +getFullName()+": "+e.getMessage(), e);
        }
    }
    
    /*
    /*****************************************************
    /* Extended API, generic
    /*****************************************************
     */

    @Override
    public int getParameterCount() {
        return getRawParameterTypes().length;
    }

    public String getFullName() {
        return getDeclaringClass().getName() + "#" + getName() + "("
            +getParameterCount()+" params)";
    }
    
    public Class<?>[] getRawParameterTypes()
    {
        if (_paramClasses == null) {
            _paramClasses = _method.getParameterTypes();
        }
        return _paramClasses;
    }
    
    public Type[] getGenericParameterTypes() {
        return _method.getGenericParameterTypes();
    }

    @Override
    public Class<?> getRawParameterType(int index)
    {
        Class<?>[] types = getRawParameterTypes();
        return (index >= types.length) ? null : types[index];
    }

    @Override
    public Type getGenericParameterType(int index)
    {
        Type[] types = _method.getGenericParameterTypes();
        return (index >= types.length) ? null : types[index];
    }

    public Class<?> getRawReturnType() {
        return _method.getReturnType();
    }
    
    public Type getGenericReturnType() {
        return _method.getGenericReturnType();
    }

    /*
    /********************************************************
    /* Other
    /********************************************************
     */

    @Override
    public String toString()
    {
        return "[method "+getFullName()+"]";
    }
}
