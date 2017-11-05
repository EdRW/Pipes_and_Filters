package pipes;

public class PipeClosedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -579265017627968258L;

	PipeClosedException(String msg) {
		super(msg);
	}
}
