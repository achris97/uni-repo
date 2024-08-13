import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.net.MalformedURLException; 
import java.rmi.NotBoundException;
import java.util.*;
 
public class HRClient extends java.rmi.server.UnicastRemoteObject implements QueueListener {
	
	protected HRClient() throws RemoteException
    {
    }
 
    public static void main(String[] args) { 
        try {
			//Depending on the arguments, client calls the appropriate method of the interface
			if (args.length == 0) {
				reservationManual();
			} else if (args.length == 2) {
				HRInterface hr = (HRInterface)Naming.lookup("rmi://" + args[1] + "/HRInterface");
				if (args[0].equals("list")) System.out.println(hr.list());
				if (args[0].equals("guests")) System.out.println(hr.guests());
				
			} else if (args.length == 5) {
				HRInterface hr = (HRInterface)Naming.lookup("rmi://" + args[1] + "/HRInterface");
				char type = args[2].charAt(0);
				int number = Integer.parseInt(args[3]);
				String name = args[4];
				String answer = "";
				if (args[0].equals("book")) {
					
					answer = hr.book(type, number, name);
					System.out.println(answer);
					//"Cannot proceed with reservation" message -> No available rooms. Client selects either to be added in a waiting list or not.
					if (answer.contains("proceed")) {
						System.out.print("Do you want to receive a notification when rooms will be available again [Y/N]? ");
						Scanner reader = new Scanner(System.in);
						char c = reader.next().charAt(0);
						if ( c == 'Y' || c == 'y') {
							HRClient client = new HRClient();
							hr.clientQueue(client, type);
						}
						reader.close();
					}		
				}
				if (args[0].equals("cancel")) System.out.println(hr.cancel(type, number, name));	
			} else {
				System.out.println("Run \'HRClient\' to see the app manual.");
			}			
        } 
        catch (MalformedURLException murle) { 
            System.out.println(); 
            System.out.println(
              "MalformedURLException"); 
            System.out.println(murle); 
        } 
        catch (RemoteException re) { 
            System.out.println(); 
            System.out.println(
                        "RemoteException"); 
            System.out.println(re); 
        } 
        catch (NotBoundException nbe) { 
            System.out.println(); 
            System.out.println(
                       "NotBoundException"); 
            System.out.println(nbe); 
        }  
    }
	
	private static void reservationManual() {
		System.out.println("---------------- How to use ----------------");
		System.out.println("List of available rooms:\n\tlist <hostname>");
		System.out.println("List of hotel guests:\n\tguests <hostname>");
		System.out.println("Book a number of rooms of a specific type in the given name:\n\tbook <hostname> <type> <number> <name>");
		System.out.println("Cancel a number of rooms of a specific type booked by the given name:\n\tcancel <hostname> <type> <number> <name>");
		System.out.println("---------------------------- 	----------------");
	}
	
	public void roomAvailable (String message) {
		System.out.println(message);
	}
} 