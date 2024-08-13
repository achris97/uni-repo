public interface HRInterface extends java.rmi.Remote { 
    
	public String list () throws java.rmi.RemoteException;
		
	public String book (char type, int number, String name) throws java.rmi.RemoteException;
		
	public String guests ()	throws java.rmi.RemoteException;
		
	public String cancel (char type, int number, String name) throws java.rmi.RemoteException;
		
	public void clientQueue (QueueListener client, char type) throws java.rmi.RemoteException;
	
	public void removeClientFromQueue (QueueListener client, char type) throws java.rmi.RemoteException;
} 