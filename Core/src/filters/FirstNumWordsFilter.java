package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

/*
 * takes number and passes only the first few items up until
 * the number of items configured. The rest of the items get thrown
 * away.
 */
public class FirstNumWordsFilter extends Filter<String, String> {
	int numItems;

	public FirstNumWordsFilter(IPipe<String> readPipe, IPipe<String> writePipe, int numItems) {
		super(readPipe, writePipe);
		this.numItems = numItems;
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<String> write) throws InterruptedException, PipeClosedException {
		String word;
		int i = 0;
		
		while ((word = readPipe.blockingRead()) != null) {
			debugger.tick();
			
			if(i < numItems) {
				writePipe.blockingWrite(word);
			}
			i++;
			debugger.tock();
		}
	}

}
