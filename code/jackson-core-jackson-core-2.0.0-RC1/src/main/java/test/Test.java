package test;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		JsonFactory f = new JsonFactory();
		//		File file = new File("d:/test.json");
		//		JsonParser parser = f.createJsonParser(file);
		//		while (parser.nextToken() != JsonToken.END_OBJECT)
		//		{
		//			String fieldname = parser.getCurrentName();
		//			if ("name".equals(fieldname))
		//			{
		//				//当前结点为name   
		//				parser.nextToken();
		//				System.out.println(parser.getText()); // 输出 mkyong  
		//			}
		//		}

		JsonGenerator g = f.createJsonGenerator(new File("d:/test2.json"), JsonEncoding.UTF8);
		g.writeStartObject(); // {  

		g.writeStringField("name", "mkyong"); // "name" : "mkyong"  
		g.writeNumberField("age", 29); // "age" : 29  

		g.writeFieldName("messages"); // "messages" :  
		g.writeStartArray(); // [  

		g.writeString("msg 1"); // "msg 1"  
		g.writeString("msg 2"); // "msg 2"  
		g.writeString("msg 3"); // "msg 3"  

		g.writeEndArray(); // ]  

		g.writeEndObject(); // }  

		g.close();

	}

}
