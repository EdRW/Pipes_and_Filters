package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

public class ToLowerCaseFilter extends Filter<String, String> {

	public ToLowerCaseFilter(IPipe<String> readPipe, IPipe<String> writePipe) {
		super(readPipe, writePipe);
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<String> write) throws InterruptedException, PipeClosedException {
		String word;
		while ((word = readPipe.blockingRead()) != null) {
			String loweredWord = word.toLowerCase();
			writePipe.blockingWrite(loweredWord);
			System.out.println("ToLowerCaseFilter: " + loweredWord);
		}
	}

}
