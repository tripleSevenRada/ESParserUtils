import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineLength {

	private static final String INPUT_ENCODING = "UTF-8";
	
	public static void main(String[] args) {
		if(args.length != 2) { printUsage(); return; }
		String pathS = args [0];
		int lengthLimit;
		try {
		lengthLimit = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printUsage(); return;
		}
		parseInputFile(pathS, lengthLimit);
	}

	private static int lineNmb = 1;
	private static void parseInputFile(String path, int limit) {
		Reader reader = null;
		BufferedReader bufferedReader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(path), INPUT_ENCODING);
			bufferedReader = new BufferedReader(reader); 
			while(true) {
				String line = bufferedReader.readLine();
				if(line == null) {
					break;
				}
				processLine(line, limit, lineNmb);
				lineNmb ++;
			}
		} catch (IOException e) { 
			printIOExc(e.getMessage());
			return;
		} finally {
			try {
				if(reader != null)reader.close();
				if(bufferedReader != null)bufferedReader.close();
			} catch (IOException e) {
				printIOExc(e.getMessage());
				return;
			}
		}
		printStats();
	}
	
	
	//core data structures
	private static List <String> trailingOrLeadingWhitespace = new ArrayList<String>();
	private static List <Integer> trailingOrLeadingWhitespaceLineNmbs = new ArrayList<Integer>();
	
	private static Map <String, Integer> valueToLineNmb = new HashMap<String, Integer>();
	private static Map <Integer, String> overLimitToValue = new HashMap<Integer, String>();
	
	
	private static void processLine(String line, int limit, int lineNmb) {
		String lineTrimmed = line.trim();
		if(line.length() != lineTrimmed.length()) {
			trailingOrLeadingWhitespace.add(line);
			trailingOrLeadingWhitespaceLineNmbs.add(lineNmb);
		}
		if(line.length() > limit) {
			overLimitToValue.put(line.length(), line);
			valueToLineNmb.put(line, lineNmb);
		}
	}
	
	private static void printStats() {
		System.out.println("+++++++++++++++++++++++++ MEZERY NA KONCI NEBO ZACATKU: +++++++++++++++++++++++++");
		for(int i = 0; i < trailingOrLeadingWhitespace.size(); i++) {
			System.out.println(trailingOrLeadingWhitespaceLineNmbs.get(i) + " - >" + trailingOrLeadingWhitespace.get(i) + "<");
		}
		System.out.println("++++++++++++++++++++++++++++++++++ PRES LIMIT: ++++++++++++++++++++++++++++++++++");
		List<Integer> overLimitSortable = new ArrayList<Integer>(overLimitToValue.keySet());
		Collections.sort(overLimitSortable);
		Collections.reverse(overLimitSortable);
		for(Integer length : overLimitSortable) {
			String text = overLimitToValue.get(length);
			Integer lineNmb = valueToLineNmb.get(text);
			System.out.println("delka: " + length + " radek: " + lineNmb + " text: >" + text + "<");
		}
	}                   
	
	private static void printUsage() {
		System.out.println("Jak na to: arg 1 je path, 2 delka radku (limit)");
		System.out.println("Input character set: " + INPUT_ENCODING);
	}
	
	private static void printIOExc(String message) {
		System.out.println("IO chyba " + message);
	}
}
