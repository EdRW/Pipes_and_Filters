package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

public class NonAlphaFilter extends Filter<String, String> {

	public NonAlphaFilter(IPipe<String> readPipe, IPipe<String> writePipe) {
		super(readPipe, writePipe);
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<String> write) throws InterruptedException, PipeClosedException {
		String word;
		while ((word = readPipe.blockingRead()) != null) {
			if (word.matches("[A-Za-z]+")) {
				writePipe.blockingWrite(word);
				System.out.println("NonAlphaFilter: " + word);
			}
		}
	}

}
