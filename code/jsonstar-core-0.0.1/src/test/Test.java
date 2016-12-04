package test;

import java.io.IOException;

import com.jsonstar.core.io.IOContext;
import com.jsonstar.core.util.BufferRecycler;
import com.jsonstar.core.util.TextBuffer;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		BufferRecycler r = new BufferRecycler();
		//»Ì“˝”√
		TextBuffer tb = new TextBuffer(r);
		tb.append('w');
		tb.append('x');
		tb.append('f');
		System.out.println(tb.toString());

		IOContext io = new IOContext(r, null, true);
		char[] buf = io.allocConcatBuffer();
		buf[0] = 'w';
		io.releaseConcatBuffer(buf);

	}

}
