package filters;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;

/*
 * Sorts a Hashmap of string, int into order based on the int.
 */
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
			debugger.tick();
			
			Map<String, Integer> sortedTerms = sortByValue(termFrequency);
			for (String term : sortedTerms.keySet()) {
				//System.out.println("attempting to write " + term + "to pipe with remaining capacity: " + writePipe.remainingCapacity());
				writePipe.blockingWrite(term);
				
				debugger.tock();
				if (Debugger.loggingStatus()) System.out.println("SortedFreqFilter: " + term + ", " + termFrequency.get(term));
			}			
		}
	}
	
	/*
	 * Found this Hashmap sorting code on
	 * https://www.programcreek.com/2013/03/java-sort-map-by-value/ 
	 */	
	private Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {
		Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
	
	class ValueComparator implements Comparator<String> {
		Map<String, Integer> map;
	 
		public ValueComparator(Map<String, Integer> map) {
			this.map = map;
		}
	 
		public int compare(String keyA, String keyB) {
			Integer valueA = (Integer) map.get(keyA);
			Integer valueB = (Integer) map.get(keyB);
			int returnVal = valueB.compareTo(valueA);
			if (returnVal == 0) {
				returnVal = ((String)keyA).compareTo(((String)keyB));
			}
			return returnVal;
		}

	}
}
