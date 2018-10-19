package filters;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;


/*
 * abstract class from which all filters should inherit
 * concrete filters only need to implement the filter method
 * this abstract class handles the rest.
 */
public abstract class Filter<T, S> implements Runnable{
	
	protected final IPipe<T> readPipe;
	protected final IPipe<S> writePipe;
	
	protected final Debugger debugger;

	protected Filter(IPipe<T> readPipe, IPipe<S> writePipe) {
		this.readPipe = readPipe;
		this.writePipe = writePipe;
		debugger = new Debugger(this.getClass().getName());
	}

	@Override
	public void run() {
		debugger.start();
		
		try {
			filter(readPipe, writePipe);
			writePipe.blockingClose();
		} catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
		debugger.stop();
		debugger.report();
	}

	protected abstract void filter(IPipe<T> read, IPipe<S> write) 
			throws InterruptedException, PipeClosedException;
	
}
