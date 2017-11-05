package pipes;

public interface IPipe<T> {
	
	public T blockingRead() throws InterruptedException, PipeClosedException;
	
	public void blockingWrite(T data) throws InterruptedException, PipeClosedException;
	
	public void blockingClose() throws InterruptedException;
	
	public void unsafeClose();
	
	public int remainingCapacity();
	
	public int size();
}
