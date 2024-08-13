import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.*;

public class HRServer {

   public HRServer() {
     	try {
       		HRInterface c = new HRImpl();
			Naming.rebind("rmi://localhost/HRInterface", c);
     	} catch (Exception e) {
       		System.out.println("Trouble: " + e);
     	}
   }

   public static void main(String args[]) {
	   try {
		   System.out.println("Hotel Reservation server started.");
		   LocateRegistry.createRegistry(1099);
	       System.out.println("java RMI registry created.");
	    } catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}
	   new HRServer();
   }
}
