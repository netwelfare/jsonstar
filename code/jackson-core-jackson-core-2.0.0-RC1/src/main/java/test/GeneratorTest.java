package test;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator.Feature;

public class GeneratorTest
{

	public static void main(String[] args) throws JsonGenerationException, IOException
	{
		//		BufferRecycler br = new BufferRecycler();
		//		IOContext ctxt = new IOContext(br, null, true);
		//		UTF8JsonGenerator g = new UTF8JsonGenerator(ctxt, Feature.QUOTE_FIELD_NAMES.ordinal(), null, null);
		//		JsonWriteContext temp = g.getOutputContext();
		//		temp = temp.createChildObjectContext();
		//		//g.writeFieldName("name");
		//		//g.writeStringField("name", "wxf");
		//		g.writeStartObject();
		//		//g.writeFieldName("name");
		//		g.writeStringField("name", "wxf");
		//		g.writeEndObject();
		//		g.close();
		//		g.flush();

		int x = Feature.QUOTE_FIELD_NAMES.ordinal();
		System.out.println(1 << x);
	}

}
