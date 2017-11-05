package filters;

import java.util.HashMap;

import pipes.IPipe;
import pipes.PipeClosedException;

public class WordFrequencyFilter extends Filter<String, HashMap<String, Integer>> {
	HashMap<String, Integer> termFrequency;

	public WordFrequencyFilter(IPipe<String> readPipe, IPipe<HashMap<String, Integer>> writePipe) {
		super(readPipe, writePipe);
		this.termFrequency = new HashMap<>();
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<HashMap<String, Integer>> write)
			throws InterruptedException, PipeClosedException {
		String word;
		while ((word = readPipe.blockingRead()) != null) {
			if (termFrequency.containsKey(word)) {
				termFrequency.put(word, termFrequency.get(word) + 1);
			} else {
				termFrequency.put(word, 1);
			}			
		}	
		
		writePipe.blockingWrite(termFrequency);
		System.out.println("TermFrequencyFilter: " + termFrequency);
	}

}
