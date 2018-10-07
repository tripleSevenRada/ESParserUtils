import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ESParser {
	
	private static final String CRLF = "\r\n";
	private static final String SEP = File.separator;
	private static final String EMPTY_TITLE = "/";
	private static final String NEW_TITLE = ".";
	private static final String EMPTY_MARKUP = "<c>" + CRLF;
	private static final String EMPTY_LINE = CRLF;
	private static final String ONE_TWO_FILE_NAME = "1_2tab";
	private static final String THREE_FOUR_FILE_NAME = "3_4tab";
	private static final String OUTPUT_FILE_EXT = ".txt";
	private static final String INPUT_ENCODING = "UTF-8";
	private static final Charset OUTPUT_ENCODING = Charset.forName("UTF-8");//windows-1250//ISO-8859-2//UTF-8
	private Map <Integer, Integer> nmbLinesToFreq = new HashMap <Integer, Integer>();
	private int nmbTitles;
	private int emptyLines;
	private List <String> currTitle = new ArrayList<String>();
	private List <String> fileOneTwo = new LinkedList<String>();
	private List <String> fileThreeFour = new LinkedList<String>();
	
	public static void main(String [] args) {
		if(args.length != 1) { printUsage(); return; }
		ESParser parser = new ESParser();
		parser.parse(args[0]);
	}
	
	private void parse(String pathS) {
		printUsage();
		Reader reader = null;
		BufferedReader bufferedReader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(pathS), INPUT_ENCODING);
			bufferedReader = new BufferedReader(reader); 
			while(true) {
				String line = bufferedReader.readLine();
				if(line == null) {
					processTitle();
					currTitle.clear();
					break;
				}
				processLine(line.trim());
			}
		} catch (IOException e) {
			printIOExc(e.getMessage());
		} finally {
			try {
				if(reader != null)reader.close();
				if(bufferedReader != null)bufferedReader.close();
			} catch (IOException e) {
				printIOExc(e.getMessage());
			}
		}
		printStats();
		
		//OUTPUT
		String inputFileName = pathS.substring(pathS.lastIndexOf(SEP) + 1, pathS.lastIndexOf(".") == -1 ? pathS.length() : pathS.lastIndexOf("."));
		String outputPathOneTwo = pathS.substring(0, pathS.lastIndexOf(SEP) + 1) + ONE_TWO_FILE_NAME +"_" + inputFileName + OUTPUT_FILE_EXT;
		System.out.println("Writing into: " + outputPathOneTwo);
		writeOutputFile(outputPathOneTwo, fileOneTwo);
		String outputPathThreeFour = pathS.substring(0, pathS.lastIndexOf(SEP) + 1) + THREE_FOUR_FILE_NAME + "_" + inputFileName + OUTPUT_FILE_EXT;
		System.out.println("Writing into: " + outputPathThreeFour);
		writeOutputFile(outputPathThreeFour, fileThreeFour);
	}
	
	private void processLine(String line) {
		//System.out.println("LINE: " + line);
		if(line.equals(EMPTY_TITLE)) {
			processTitle();
			currTitle.clear();
			putEmpty();
			return;
		}
		if(line.equals(NEW_TITLE)) {
			processTitle();
			currTitle.clear();
			return;
		}
		if(line.length() == 0) {
			emptyLines ++;
			return;
		} 
		currTitle.add(line + CRLF);
	}
	
	private void processTitle() {
		if(currTitle.size() == 0) return;
		if(nmbLinesToFreq.containsKey(currTitle.size())){
			int nmb = nmbLinesToFreq.get(currTitle.size());
			nmbLinesToFreq.put(currTitle.size(), nmb + 1);
		} else {
			nmbLinesToFreq.put(currTitle.size(), 1);
		}
		
		int fullCycles = currTitle.size() / 4;
		int remnant = currTitle.size() % 4;
		
		int c = 0;
		for(int full = 0; full < fullCycles; full ++) {
			fileOneTwo.add(currTitle.get(c)); c++;
			fileOneTwo.add(currTitle.get(c)); c++;
			fileThreeFour.add(currTitle.get(c)); c++;
			fileThreeFour.add(currTitle.get(c)); c++;
		}
		
		switch (remnant) {
		
			case 0: {break;}
		
			case 1: {
				fileOneTwo.add(currTitle.get(c));
				fileOneTwo.add(EMPTY_LINE);
				fileThreeFour.add(EMPTY_MARKUP);
				fileThreeFour.add(EMPTY_LINE);
				break;
			}
		
			case 2: {
				fileOneTwo.add(currTitle.get(c)); c++;
				fileOneTwo.add(currTitle.get(c));
				fileThreeFour.add(EMPTY_MARKUP);
				fileThreeFour.add(EMPTY_LINE);		
				break;
			}
		
			case 3: {
				fileOneTwo.add(currTitle.get(c)); c++;
				fileOneTwo.add(currTitle.get(c)); c++;
				fileThreeFour.add(currTitle.get(c));
				fileThreeFour.add(EMPTY_LINE);			
				break;
			}
		
		}
		
		nmbTitles ++;
	}
	
	private void putEmpty() {
		fileOneTwo.add(EMPTY_MARKUP);
		fileOneTwo.add(EMPTY_LINE);
		fileThreeFour.add(EMPTY_MARKUP);
		fileThreeFour.add(EMPTY_LINE);
	}
	
	private void printStats() {
		System.out.println("--------------------------------------------------");
		System.out.println("Titulku je: " + nmbTitles);
		System.out.println("Pocet radku -> pocet titulku:");
		List<Integer> keySetSortable = new LinkedList<Integer>(nmbLinesToFreq.keySet());
		Collections.sort(keySetSortable);
		for(Integer i : keySetSortable) {
			System.out.println(i + " -> " + nmbLinesToFreq.get(i));
		}
		System.out.println("Prazdnych radku: " + emptyLines);
	}
		
	private void printIOExc(String message) {
		System.out.println("IO chyba " + message);
	}
	
	private static void printUsage() {
		System.out.println("Jak na to: arg 1 je path");
		System.out.println("Prazdny titulek: " + EMPTY_TITLE);
		System.out.println("Novy titulek: " + NEW_TITLE);
		System.out.println("Input character set: " + INPUT_ENCODING);
		System.out.println("Output character set: " + OUTPUT_ENCODING.toString());
	}
	
	private void writeOutputFile(String pathS, List<String> content) {
		
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(pathS), 
			        OUTPUT_ENCODING);
			for(String line : content) {
				osw.write(line);
			}
		} catch (FileNotFoundException e) {
			printIOExc(e.getMessage());
		} catch (IOException e) {
			printIOExc(e.getMessage());
		} finally {
			try {
				if (osw != null) osw.close();
			} catch (IOException e) {
				printIOExc(e.getMessage());
			}
		}
	}
}
