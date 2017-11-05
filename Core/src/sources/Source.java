package sources;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;

public abstract class Source<T> implements Runnable {

	protected final IPipe<T> writePipe;
	
	protected final Debugger debugger;

	protected Source(IPipe<T> writePipe) {
		this.writePipe = writePipe;
		debugger = new Debugger(this.getClass().getName());
	}
	
	@Override
	public void run() {
		debugger.start();
		try {
			generateItems(writePipe);
			writePipe.blockingClose();
		}catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
		debugger.stop();
		debugger.report();		
	}
	
	protected abstract void generateItems(IPipe<T> write) throws InterruptedException, PipeClosedException;
}
