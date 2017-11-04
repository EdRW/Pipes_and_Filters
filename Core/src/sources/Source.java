package sources;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Source<T> implements Runnable {

	private IPipe<T> writePipe;

	Source(IPipe<T> writePipe) {
		this.writePipe = writePipe;
	}
	
	@Override
	public void run() {
		try {
			T item;
			while ((item = readItem()) != null) {
				writePipe.blockingWrite(item);
			}
			
			writePipe.blockingClose();
			
		}catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
	}
	
	protected abstract T readItem(); 

}
