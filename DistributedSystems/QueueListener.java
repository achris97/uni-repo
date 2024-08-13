public interface QueueListener extends java.rmi.Remote {
	
	void roomAvailable (String message) throws java.rmi.RemoteException;
}