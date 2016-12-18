package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.format.DataFormatDetector;
import com.fasterxml.jackson.core.format.DataFormatMatcher;

public class Format
{

	public static void main(String[] args) throws UnsupportedEncodingException, IOException
	{
		JsonFactory jsonF = new JsonFactory();
		DataFormatDetector detector = new DataFormatDetector(jsonF);
		final String JSON = "{  \"field\" : true }";
		DataFormatMatcher matcher = detector.findFormat(new ByteArrayInputStream(JSON.getBytes("UTF-8")));
		// should have match
		System.out.println(matcher.hasMatch());
		System.out.println(matcher.getMatchedFormatName());
		System.out.println(matcher.getMatch());
		// no "certain" match with JSON, but solid:
		System.out.println(matcher.getMatchStrength());
		// and thus:
		JsonParser jp = matcher.createParserWithMatch();
		System.out.println(jp.nextToken());
		System.out.println(jp.nextToken());
		System.out.println(jp.getCurrentName());
		System.out.println(jp.nextToken());
		System.out.println(jp.nextToken());
		System.out.println(jp.nextToken());
		jp.close();

	}

}
