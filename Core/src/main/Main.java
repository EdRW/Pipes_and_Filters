package main;

import java.io.FileNotFoundException;
import java.util.HashMap;

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
import utils.Debugger;

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
		
		IPipe<String> pipe1 = new StringPipe(5000);
		IPipe<String> pipe2 = new StringPipe(5000);
		IPipe<String> pipe3 = new StringPipe(5000);
		IPipe<String> pipe4 = new StringPipe(5000);
		IPipe<String> pipe5 = new StringPipe(5000);
		IPipe<HashMap<String, Integer>> pipe6 = new StrIntHashMapPipe(5000);
		IPipe<String> pipe7 = new StringPipe(5000);
		IPipe<String> pipe8 = new StringPipe(5000);
		
		try {
			Source<String> source = new TextFileWordSource(pipe1, "kjbible.txt");
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
			
			Debugger debugger = new Debugger("Main");
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
			debugger.report();
			
		} catch (FileNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
