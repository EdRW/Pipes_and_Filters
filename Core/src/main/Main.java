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

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// needs into intake and parse args to determine which source files, pipes, and filters will be used
		
		// Source can read in files maybe takes a text file name
		
		// Filters takes input and output pipes
		
		// Sinks at least one will print out the list of 10 most frequest occuring terms listed in alphabetical order
		
		/*
		 * Overall pipe filter system for this assignment should like this with pipes : ->
		 * 
		 * [Source] -<bq word string pipe>->  {Remove Stop Words} -<bq word string pipe>-> 
		 * {Remove non-alphas} -<bq word string pipe>-> {Make Lower Case} -<bq word string pipe>-> 
		 * {Stem to root terms} -<bq word string pipe>-> {Compute term freq} -<bq string, int dictionary pipe>->
		 *  [Sink: prints out terms]
		 */
		
		IPipe<String> pipe1 = new StringPipe(5000);
		IPipe<String> pipe2 = new StringPipe(5000);
		IPipe<String> pipe3 = new StringPipe(5000);
		IPipe<String> pipe4 = new StringPipe(5000);
		IPipe<String> pipe5 = new StringPipe(5000);
		IPipe<HashMap<String, Integer>> pipe6 = new StrIntHashMapPipe(5000);
		IPipe<String> pipe7 = new StringPipe(5000);
		IPipe<String> pipe8 = new StringPipe(5000);
		
		try {
			Source<String> source = new TextFileWordSource(pipe1, "usdeclar.txt");
			Filter<String, String> nonAlphaFilter = new NonAlphaFilter(pipe1, pipe2);
			Filter<String, String> toLowerCaseFilter = new ToLowerCaseFilter(pipe2, pipe3);
			Filter<String, String> stopWordFilter = new StopWordFilter(pipe3, pipe4, "stopwords.txt");
			Filter<String, String> porterStemmerFilter = new PorterStemmerFilter(pipe4, pipe5);
			Filter<String, HashMap<String, Integer>> wordFrequencyFilter = new WordFrequencyFilter(pipe5, pipe6);
			Filter<HashMap<String, Integer>, String> sortByFrequencyFilter = new SortByFrequencyFilter(pipe6, pipe7);
			Filter<String, String> firstNumWordsFilter = new FirstNumWordsFilter(pipe7, pipe8, 10);
			Sink<String> printSink = new PrintSink(pipe8);
			
			source.run();
			nonAlphaFilter.run();
			toLowerCaseFilter.run();
			stopWordFilter.run();
			porterStemmerFilter.run();
			wordFrequencyFilter.run();
			sortByFrequencyFilter.run();
			firstNumWordsFilter.run();
			printSink.run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
