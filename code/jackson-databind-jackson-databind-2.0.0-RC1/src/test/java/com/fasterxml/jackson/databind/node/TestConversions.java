package com.fasterxml.jackson.databind.node;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Assert;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * Unit tests for verifying functionality of {@link JsonNode} methods that
 * convert values to other types
 */
public class TestConversions extends BaseMapTest
{
    static class Root {
        public Leaf leaf;
    }

    static class Leaf {
        public int value;
    }
    
    // MixIn for [JACKSON-554]
    @JsonDeserialize(using = LeafDeserializer.class)
    public static class LeafMixIn
    {
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */
    
    public void testAsInt() throws Exception
    {
        assertEquals(9, IntNode.valueOf(9).asInt());
        assertEquals(7, LongNode.valueOf(7L).asInt());
        assertEquals(13, new TextNode("13").asInt());
        assertEquals(0, new TextNode("foobar").asInt());
        assertEquals(27, new TextNode("foobar").asInt(27));
        assertEquals(1, BooleanNode.TRUE.asInt());
    }

    public void testAsBoolean() throws Exception
    {
        assertEquals(false, BooleanNode.FALSE.asBoolean());
        assertEquals(true, BooleanNode.TRUE.asBoolean());
        assertEquals(false, IntNode.valueOf(0).asBoolean());
        assertEquals(true, IntNode.valueOf(1).asBoolean());
        assertEquals(false, LongNode.valueOf(0).asBoolean());
        assertEquals(true, LongNode.valueOf(-34L).asBoolean());
        assertEquals(true, new TextNode("true").asBoolean());
        assertEquals(false, new TextNode("false").asBoolean());
        assertEquals(false, new TextNode("barf").asBoolean());
        assertEquals(true, new TextNode("barf").asBoolean(true));

        assertEquals(true, new POJONode(Boolean.TRUE).asBoolean());
    }
    
    // Deserializer to trigger the problem described in [JACKSON-554]
    public static class LeafDeserializer extends JsonDeserializer<Leaf>
    {
        @Override
        public Leaf deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException
        {
            JsonNode tree = (JsonNode) jp.readValueAsTree();
            Leaf leaf = new Leaf();
            leaf.value = tree.get("value").intValue();
            return leaf;
        }
    }

    // Test for [JACKSON-554]
    public void testTreeToValue() throws Exception
    {
        String JSON = "{\"leaf\":{\"value\":13}}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixInAnnotations(Leaf.class, LeafMixIn.class);
        JsonNode root = mapper.readTree(JSON);
        // Ok, try converting to bean using two mechanisms
        Root r1 = mapper.treeToValue(root, Root.class);
        assertNotNull(r1);
        assertEquals(13, r1.leaf.value);
    }

    // Test for [JACKSON-631]
    public void testBase64Text() throws Exception
    {
        // let's actually iterate over sets of encoding modes, lengths
        
        final int[] LENS = { 1, 2, 3, 4, 7, 9, 32, 33, 34, 35 };
        final Base64Variant[] VARIANTS = {
                Base64Variants.MIME,
                Base64Variants.MIME_NO_LINEFEEDS,
                Base64Variants.MODIFIED_FOR_URL,
                Base64Variants.PEM
        };

        for (int len : LENS) {
            byte[] input = new byte[len];
            for (int i = 0; i < input.length; ++i) {
                input[i] = (byte) i;
            }
            for (Base64Variant variant : VARIANTS) {
                TextNode n = new TextNode(variant.encode(input));
                byte[] data = null;
                try {
                    data = n.getBinaryValue(variant);
                } catch (Exception e) {
                    throw new IOException("Failed (variant "+variant+", data length "+len+"): "+e.getMessage(), e);
                }
                assertNotNull(data);
                assertArrayEquals(data, input);
            }
        }
    }

    static class Issue709Bean {
        public byte[] data;
    }
    
    /**
     * Simple test to verify that byte[] values can be handled properly when
     * converting, as long as there is metadata (from POJO definitions).
     */
    public void testIssue709() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        byte[] inputData = new byte[] { 1, 2, 3 };
        ObjectNode node = mapper.createObjectNode();
        node.put("data", inputData);
        Issue709Bean result = mapper.treeToValue(node, Issue709Bean.class);
        String json = mapper.writeValueAsString(node);
        Issue709Bean resultFromString = mapper.readValue(json, Issue709Bean.class);
        Issue709Bean resultFromConvert = mapper.convertValue(node, Issue709Bean.class);
        
        // all methods should work equally well:
        Assert.assertArrayEquals(inputData, resultFromString.data);
        Assert.assertArrayEquals(inputData, resultFromConvert.data);
        Assert.assertArrayEquals(inputData, result.data);
    }

    public void testEmbeddedObject() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buf = new TokenBuffer(mapper);
        buf.writeObject(new byte[0]);
        JsonNode node = mapper.readTree(buf.asParser());
        assertTrue(node.isPojo());
        assertEquals(byte[].class, ((POJONode) node).getPojo().getClass());
    }

    public void testEmbeddedObjectInArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buf = new TokenBuffer(mapper);
        buf.writeStartArray();
        buf.writeObject(new byte[0]);
        buf.writeEndArray();
        JsonNode node = mapper.readTree(buf.asParser());
        assertTrue(node.isArray());
        assertEquals(1, node.size());
        JsonNode n = node.get(0);
        assertTrue(n.isPojo());
        assertEquals(byte[].class, ((POJONode) n).getPojo().getClass());
    }

    public void testEmbeddedObjectInObject() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buf = new TokenBuffer(mapper);
        buf.writeStartObject();
        buf.writeFieldName("pojo");
        buf.writeObject(new byte[0]);
        buf.writeEndObject();
        JsonNode node = mapper.readTree(buf.asParser());
        assertTrue(node.isObject());
        assertEquals(1, node.size());
        JsonNode n = node.get("pojo");
        assertTrue(n.isPojo());
        assertEquals(byte[].class, ((POJONode) n).getPojo().getClass());
    }
}

