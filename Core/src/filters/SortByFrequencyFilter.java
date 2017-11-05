package filters;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pipes.IPipe;
import pipes.PipeClosedException;

public class SortByFrequencyFilter extends Filter<HashMap<String, Integer>, String> {

	public SortByFrequencyFilter(IPipe<HashMap<String, Integer>> readPipe,
			IPipe<String> writePipe) {
		super(readPipe, writePipe);
	}

	@Override
	protected void filter(IPipe<HashMap<String, Integer>> read, IPipe<String> write)
			throws InterruptedException, PipeClosedException {
		HashMap<String, Integer> termFrequency;
		while ((termFrequency = readPipe.blockingRead()) != null) {
			Map<String, Integer> sortedTerms = sortByValue(termFrequency);
			for (String term : sortedTerms.keySet()) {
				writePipe.blockingWrite(term);
				System.out.println("SortedFreqFilter: " + term + ", " + termFrequency.get(term));
			}			
		}
	}
	
	/*
	 * Found this Hashmap sorting code on
	 * https://www.programcreek.com/2013/03/java-sort-map-by-value/ 
	 */	
	private Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
	
	class ValueComparator implements Comparator {
		Map map;
	 
		public ValueComparator(Map map) {
			this.map = map;
		}
	 
		public int compare(Object keyA, Object keyB) {
			Comparable valueA = (Comparable) map.get(keyA);
			Comparable valueB = (Comparable) map.get(keyB);
			int returnVal = valueB.compareTo(valueA);
			if (returnVal == 0) {
				returnVal = ((Comparable)keyA).compareTo(((Comparable)keyB));
			}
			return returnVal;
		}
	}
}
