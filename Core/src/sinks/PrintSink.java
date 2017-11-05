package sinks;

import pipes.IPipe;
import pipes.PipeClosedException;

public class PrintSink extends Sink<String> {

	public PrintSink(IPipe<String> readPipe) {
		super(readPipe);
	}

	@Override
	protected void handleItem(IPipe<String> read) throws InterruptedException, PipeClosedException {
		String word;
		while ((word = readPipe.blockingRead()) != null) {
			debugger.tick();
			
			System.out.println(word);
			
			debugger.tock();
		}
	}

}
