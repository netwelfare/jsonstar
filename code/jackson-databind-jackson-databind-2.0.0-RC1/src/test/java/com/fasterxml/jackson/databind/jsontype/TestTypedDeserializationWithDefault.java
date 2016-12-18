package com.fasterxml.jackson.databind.jsontype;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests related to [JACKSON-712]; specialized handling of
 * otherwise invalid type id embedding cases.
 */
public class TestTypedDeserializationWithDefault extends BaseMapTest
{
  private final ObjectMapper mapper = new ObjectMapper();

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = LegacyInter.class)
  @JsonSubTypes(value = {@JsonSubTypes.Type(name = "mine", value = MyInter.class)})
  public static interface Inter { }

  public static class MyInter implements Inter
  {
    @JsonProperty("blah")
    public List<String> blah;
  }

  public static class LegacyInter extends MyInter
  {
    @JsonCreator
    LegacyInter(Object obj)
    {
      if (obj instanceof List) {
        blah = new ArrayList<String>();
        for (Object o : (List<?>) obj) {
          blah.add(o.toString());
        }
      }
      else if (obj instanceof String) {
        blah = Arrays.asList(((String) obj).split(","));
      }
      else {
        throw new IllegalArgumentException("Unknown type: " + obj.getClass());
      }
    }
  }

  public void testDeserializationWithObject() throws Exception
  {
    Inter inter = mapper.readValue("{\"type\": \"mine\", \"blah\": [\"a\", \"b\", \"c\"]}", Inter.class);

    assertTrue(inter instanceof MyInter);
    assertFalse(inter instanceof LegacyInter);
    assertEquals(Arrays.asList("a", "b", "c"), ((MyInter) inter).blah);
  }

  public void testDeserializationWithString() throws Exception
  {
    Inter inter = mapper.readValue("\"a,b,c,d\"", Inter.class);

    assertTrue(inter instanceof LegacyInter);
    assertEquals(Arrays.asList("a", "b", "c", "d"), ((MyInter) inter).blah);
  }

  public void testDeserializationWithArray() throws Exception
  {
    Inter inter = mapper.readValue("[\"a\", \"b\", \"c\", \"d\"]", Inter.class);

    assertTrue(inter instanceof LegacyInter);
    assertEquals(Arrays.asList("a", "b", "c", "d"), ((MyInter) inter).blah);
  }

  public void testDeserializationWithArrayOfSize2() throws Exception
  {
    Inter inter = mapper.readValue("[\"a\", \"b\"]", Inter.class);

    assertTrue(inter instanceof LegacyInter);
    assertEquals(Arrays.asList("a", "b"), ((MyInter) inter).blah);
  }
}
