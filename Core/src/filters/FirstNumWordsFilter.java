package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

public class FirstNumWordsFilter extends Filter<String, String> {
	int numItems;

	public FirstNumWordsFilter(IPipe<String> readPipe, IPipe<String> writePipe, int numItems) {
		super(readPipe, writePipe);
		this.numItems = numItems;
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<String> write) throws InterruptedException, PipeClosedException {
		String word;	
		for (int i = 0; i < numItems; i++ ) {
			if ((word = readPipe.blockingRead()) == null) break;
			writePipe.blockingWrite(word);
		}
	}

}
