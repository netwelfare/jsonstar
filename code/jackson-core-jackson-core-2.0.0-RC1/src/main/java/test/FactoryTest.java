package test;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class FactoryTest
{

	public static void main(String[] args) throws JsonParseException, IOException
	{
		JsonFactory f = new JsonFactory();
		String content = "{\"age\":123}";
		JsonParser parser = f.createJsonParser(content);
		//JsonToken token = parser.nextToken();

		while (parser.nextToken() != null)
		{
			String fieldname = parser.getCurrentName();
			System.out.println(parser.getText());

		}
	}

}
