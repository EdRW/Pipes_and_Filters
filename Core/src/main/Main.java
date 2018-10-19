package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import filters.Filter;
import filters.FirstNumWordsFilter;
import filters.NonAlphaFilter;
import filters.PorterStemmerFilter;
import filters.SortByFrequencyFilter;
import filters.StopWordFilter;
import filters.ToLowerCaseFilter;
import filters.WordFrequencyFilter;
import pipes.IPipe;
import pipes.StrIntHashMapPipe;
import pipes.StringPipe;
import sinks.PrintSink;
import sinks.Sink;
import sources.Source;
import sources.TextFileWordSource;
import utils.Combiner;
import utils.Debugger;
import utils.Splitter;

public class Main {

	public static void main(String[] args) {		
		/*
		 * [Source], -<pipe>-> , {Filter}, [Sink]
		 * 
		 * Overall pipe filter system for this assignment should like this. 
		 * 
		 * [Source] -<bq word string pipe>-> 
		 * {Remove non-alphas} -<bq word string pipe>-> {Make Lower Case} -<bq word string pipe>-> 
		 * {Remove Stop Words} -<bq word string pipe>-> {Stem to root terms} -<bq word string pipe>-> 
		 * {Compute term frequency} -<bq string, int hashmap pipe>->
		 * {Sort by Frequency} -<bq word string pipe>-> {take top 10 words} -<bq word string pipe>-> 
		 * [Sink: prints out terms]
		 */
		
		// control what debug data is printed to the console. Warning the setLoggingStatus will print an insane amount of text.
		Debugger.setLoggingStatus(false);
		Debugger.setProfilerStatus(true);
		
		try {
			Scanner reader = new Scanner(System.in);
			System.out.println("Enter a .txt filename located within the same folder at this application:");
			String fileName = reader.next();
			System.out.println();

		
		
			/* 
			 *  redesigned implementation is slower than original implementation...
			 */
			System.out.println("Top 10 most frequent terms in: " + fileName);
			//originalImplementation(fileName);
			redesignedImplementation(fileName);
			
			System.out.println();
			System.out.println("Press Enter to Exit...");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void originalImplementation(String fileName) {
		int bufferSize  = 10;
		IPipe<String> pipe1 = new StringPipe(bufferSize);
		IPipe<String> pipe2 = new StringPipe(bufferSize);
		IPipe<String> pipe3 = new StringPipe(bufferSize);
		IPipe<String> pipe4 = new StringPipe(bufferSize);
		IPipe<String> pipe5 = new StringPipe(bufferSize);
		IPipe<HashMap<String, Integer>> pipe6 = new StrIntHashMapPipe(bufferSize);
		IPipe<String> pipe7 = new StringPipe(bufferSize);
		IPipe<String> pipe8 = new StringPipe(bufferSize);
		
		try {
			Source<String> source = new TextFileWordSource(pipe1, fileName);
			Filter<String, String> nonAlphaFilter = new NonAlphaFilter(pipe1, pipe2);
			Filter<String, String> toLowerCaseFilter = new ToLowerCaseFilter(pipe2, pipe3);
			Filter<String, String> stopWordFilter = new StopWordFilter(pipe3, pipe4, "stopwords.txt");
			Filter<String, String> porterStemmerFilter = new PorterStemmerFilter(pipe4, pipe5);
			Filter<String, HashMap<String, Integer>> wordFrequencyFilter = new WordFrequencyFilter(pipe5, pipe6);
			Filter<HashMap<String, Integer>, String> sortByFrequencyFilter = new SortByFrequencyFilter(pipe6, pipe7);
			Filter<String, String> firstNumWordsFilter = new FirstNumWordsFilter(pipe7, pipe8, 10);
			Sink<String> printSink = new PrintSink(pipe8);
			
			
			Thread t1 = new Thread(source);
			Thread t2 = new Thread(nonAlphaFilter);
			Thread t3 = new Thread(toLowerCaseFilter);
			Thread t4 = new Thread(stopWordFilter);
			Thread t5 = new Thread(porterStemmerFilter);
			Thread t6 = new Thread(wordFrequencyFilter);
			Thread t7 = new Thread(sortByFrequencyFilter);
			Thread t8 = new Thread(firstNumWordsFilter);
			Thread t9 = new Thread(printSink);
			
			Debugger debugger = new Debugger("Original Main");
			debugger.start();
			
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			t6.start();
			t7.start();
			t8.start();
			t9.start();
			
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			t9.join();
						
			debugger.stop();
			System.out.println();
			debugger.report();
			
		} catch (FileNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void redesignedImplementation(String fileName) {
		try {
			int bufferSize  = 10;
			int numBranches = 1024;
			
			ArrayList<Thread> filterThreads = new ArrayList<>();
			
			IPipe<String> pipe1 = new StringPipe(bufferSize);
			IPipe<String> pipe5 = new StringPipe(bufferSize);
			
			for (int i = 0; i < numBranches; i++) {
				IPipe<String> pipe2 = new StringPipe(bufferSize);
				IPipe<String> pipe3 = new StringPipe(bufferSize);
				IPipe<String> pipe4 = new StringPipe(bufferSize);
				
				Filter<String, String> nonAlphaFilter = new NonAlphaFilter(pipe1, pipe2);
				Filter<String, String> toLowerCaseFilter = new ToLowerCaseFilter(pipe2, pipe3);
				Filter<String, String> stopWordFilter = new StopWordFilter(pipe3, pipe4, "stopwords.txt");
				Filter<String, String> porterStemmerFilter = new PorterStemmerFilter(pipe4, pipe5);
				
				Thread t2 = new Thread(nonAlphaFilter);
				Thread t3 = new Thread(toLowerCaseFilter);
				Thread t4 = new Thread(stopWordFilter);
				Thread t5 = new Thread(porterStemmerFilter);
				
				filterThreads.add(t2);
				filterThreads.add(t3);
				filterThreads.add(t4);
				filterThreads.add(t5);
					
			}
			
			IPipe<String> pipe0 = new StringPipe(bufferSize);
			IPipe<String> pipe6 = new StringPipe(bufferSize);
			IPipe<HashMap<String, Integer>> pipe7 = new StrIntHashMapPipe(bufferSize);
			IPipe<String> pipe8 = new StringPipe(bufferSize);
			IPipe<String> pipe9 = new StringPipe(bufferSize);
			
			Source<String> source = new TextFileWordSource(pipe0, fileName);
			Splitter<String> splitter = new Splitter<String>(pipe0, pipe1, numBranches);
			Combiner<String> combiner = new Combiner<String>(pipe5, pipe6, numBranches);
			Filter<String, HashMap<String, Integer>> wordFrequencyFilter = new WordFrequencyFilter(pipe6, pipe7);
			Filter<HashMap<String, Integer>, String> sortByFrequencyFilter = new SortByFrequencyFilter(pipe7, pipe8);
			Filter<String, String> firstNumWordsFilter = new FirstNumWordsFilter(pipe8, pipe9, 10);
			Sink<String> printSink = new PrintSink(pipe9);
			
			Thread t0 = new Thread(source);
			Thread t1 = new Thread(splitter);
			Thread t6 = new Thread(combiner);
			Thread t7 = new Thread(wordFrequencyFilter);
			Thread t8 = new Thread(sortByFrequencyFilter);
			Thread t9 = new Thread(firstNumWordsFilter);
			Thread t10 = new Thread(printSink);
			
			filterThreads.add(t0);
			filterThreads.add(t1);
			filterThreads.add(t6);
			filterThreads.add(t7);
			filterThreads.add(t8);
			filterThreads.add(t9);
			filterThreads.add(t10);
			
			Debugger debugger = new Debugger("Redesigned Main");
			debugger.start();
			
			for (Thread t : filterThreads) {
				t.start();
			}
			
			for (Thread t : filterThreads) {
				t.join();
			}
			
			debugger.stop();
			System.out.println();
			debugger.report();
			
		} catch (FileNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}


}
