import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordCount
{
	static Map<String, Integer> countMap = new HashMap<String, Integer>();

	public static void main(String[] args)
	{
		File file = new File("D:/git/jack-json-core/src/main/java");
		fileList(file);
		BufferedWriter bw = BigDataTool.BufferedWriter("D:/wordcount.txt");
		BigDataTool.mapColumTraverse(countMap, bw);
		BigDataTool.closeReader();
		BigDataTool.closeWriter();
	}

	public static void fileList(File file)
	{
		if (file.isFile())
		{
			System.out.println("文件: " + file.getAbsolutePath());
			BufferedReader br = BigDataTool.getBufferedReader(file.getAbsolutePath());
			String s = null;
			try
			{
				while ((s = br.readLine()) != null)
				{

					if (s.contains("*"))
					{
						//System.out.println(s);
						continue;
					}

					if (s.contains("(") || s.contains(")") || s.contains("\"") || s.contains(";") || s.contains(",")
							|| s.contains(".") || s.contains("{") || s.contains("}"))
					{
						s = s.replace("(", " ");
						s = s.replace(")", " ");
						s = s.replace("\"", " ");
						s = s.replace(";", " ");
						s = s.replace(",", " ");
						s = s.replace(".", " ");
						s = s.replace("{", " ");
						s = s.replace("}", " ");
					}

					String[] array = s.split(" ");
					for (String str : array)
					{
						str = str.trim();
						if (str.equals("") || str.equals("@Override") || str.length() == 1 || str.startsWith("'"))
						{
							continue;
						}
						BigDataTool.wordCount(str, countMap);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if (file.isDirectory())
		{
			//System.out.println("文件夹: " + file.getName());
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++)
			{
				fileList(f[i]);
			}
		}
	}

}
