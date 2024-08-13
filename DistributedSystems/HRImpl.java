import java.util.ArrayList;

public class HRImpl extends java.rmi.server.UnicastRemoteObject implements HRInterface { 
 
	private int availableA, availableB, availableC, availableD, availableE;
    
	private ArrayList<Reservation> guestsList = new ArrayList<Reservation>();
	private ArrayList<QueueListener> listenersA = new ArrayList<QueueListener>();
	private ArrayList<QueueListener> listenersB = new ArrayList<QueueListener>();
	private ArrayList<QueueListener> listenersC = new ArrayList<QueueListener>();
	private ArrayList<QueueListener> listenersD = new ArrayList<QueueListener>();
	private ArrayList<QueueListener> listenersE = new ArrayList<QueueListener>();
	
    public HRImpl() //Total available rooms for each category
        throws java.rmi.RemoteException { 
        super();		
		availableA = 25;
		availableB = 40;
		availableC = 20;
		availableD = 15;
		availableE = 10;	
    } 
	
	public String list() throws java.rmi.RemoteException {
			
		String list = "-------------------- Available Rooms --------------------\n";
		list += availableA + " rooms of type A (single rooms) - Price per night: 60€\n";
		list += availableB + " rooms of type B (double rooms) - Price per night: 80€\n";
		list += availableC + " rooms of type C (twin rooms) - Price per night: 90€\n";
		list += availableD + " rooms of type D (triple rooms) - Price per night: 115€\n";
		list += availableE + " rooms of type E (quad rooms) - Price per night: 140€\n";
		return list;
	}
	
	
	public String guests() throws java.rmi.RemoteException {
		
		String guests = "-------------------- Guests list --------------------\n";
		for (Reservation r: guestsList)
			guests += r.toString();
		guests += "-----------------------------------------------------";
		return guests;
	}
	
	public String book (char type, int number, String name) throws java.rmi.RemoteException  {
		
		if (number == 0 || name.isEmpty()) return "Please check number/name fields";
		
		switch(type) {
			
			case 'A': 
			//Rooms are available. Book them, count the total cost, create a new reservation instance and add it to guests list.
				if (number <= availableA ) { 
					availableA -= number;
					int totalCost = 60*number;
					System.out.println("New reservation. " + availableA + " remaining rooms in A category.");
					Reservation r = new Reservation(name, type, number);
					guestsList.add(r);
					return  "Successfull reservation. Total cost = " + String.valueOf(totalCost) + "€.";
					
				} else if (number > availableA && availableA != 0) {
					return "Only " + availableA + " rooms of this category are currently available.";
				} else {
					return "Cannot proceed with reservation. Rooms are no longer available.";
				}
			
			case 'B': 
				if (number <= availableB ) {
					availableB -= number;
					int totalCost = 80*number;
					System.out.println("New reservation. " + availableB + " remaining rooms in B category.");
					Reservation r = new Reservation(name, type, number);
					guestsList.add(r);
					return  "Successfull reservation. Total cost = " + String.valueOf(totalCost) + "€.";
					
				} else if (number > availableB && availableB != 0) {
					return "Only " + availableB + " rooms of this category are currently available.";
				} else {
					return "Cannot proceed with reservation. Rooms are no longer available.";
				}
				
			case 'C': 
				if (number <= availableC ) {
					availableC -= number;
					int totalCost = 90*number;
					System.out.println("New reservation. " + availableC + " remaining rooms in C category.");
					Reservation r = new Reservation(name, type, number);
					guestsList.add(r);
					return  "Successfull reservation. Total cost = " + String.valueOf(totalCost) + "€.";
					
				} else if (number > availableC && availableC != 0) {
					return "Only " + availableC + " rooms of this category are currently available.";
				} else {
					return "Cannot proceed with reservation. Rooms are no longer available.";
				}
				
			case 'D': 
				if (number <= availableD ) {
					availableD -= number;
					int totalCost = 115*number;
					System.out.println("New reservation. " + availableD + " remaining rooms in D category.");
					Reservation r = new Reservation(name, type, number);
					guestsList.add(r);
					return  "Successfull reservation. Total cost = " + String.valueOf(totalCost) + "€.";
					
				} else if (number > availableD && availableD != 0) {
					return "Only " + availableD + " rooms of this category are currently available.";
				} else {
					return "Cannot proceed with reservation. Rooms are no longer available.";
				}
				
			case 'E': 
				if (number <= availableE ) {
					availableE -= number;
					int totalCost = 140*number;
					System.out.println("New reservation. " + availableE + " remaining rooms in E category.");
					Reservation r = new Reservation(name, type, number);
					guestsList.add(r);
					return  "Successfull reservation. Total cost = " + String.valueOf(totalCost) + "€.";
					
				} else if (number > availableE && availableE != 0) {
					return "Only " + availableE + " rooms of this category are currently available.";
				} else {
					return "Cannot proceed with reservation. Rooms are no longer available.";
				}
			
			default:
					return "Room type must be A, B, C, D or E";
		}
	}
	
	public String cancel (char type, int number, String name) throws java.rmi.RemoteException {
		
		String message = "";
		int free = 0;
		for (Reservation r : guestsList) {
			
			if (r.getName().equals(name) && r.getType() == type) {
				System.out.println("Cancelation request from client: " + r.getName());
				int bookedRooms = r.getNumberOfRooms();
				
				if (number >= bookedRooms) {	//Cancel all rooms and remove client from the list.
					free = bookedRooms;
					guestsList.remove(r);
					message = "Successfull cancelation.";
				} else {					//Cancel the requested number and modify the 'number' field of the instance in the list
					free = number;
					r.setNumberOfRooms(bookedRooms - number);
					message = r.toString();
				}
				break;
			} 
		}
		if (free == 0) return "No rooms found";
		
		switch(type) {
			case 'A': availableA += free; notifyListeners(type); break;
			case 'B': availableB += free; notifyListeners(type); break;
			case 'C': availableC += free; notifyListeners(type);  break;
			case 'D': availableD += free; notifyListeners(type); break;
			case 'E': availableE += free; notifyListeners(type); break;
		}
		return message;
	}
	
	/*For RMI callback. 
	When a client doesn't find rooms in the wanted category and wants to wait until they are available again, will be added to the list for 
	requested rooms.*/
	public void clientQueue (QueueListener client, char type) {
		
		switch (type) {
			case 'A': listenersA.add(client); break;
			case 'B': listenersB.add(client); break;
			case 'C': listenersC.add(client); break;
			case 'D': listenersD.add(client); break;
			case 'E': listenersE.add(client); break;
		}
		System.out.println("Client added to queue for type " + type + " rooms");
	}
	
	//When a room has been released, all clients waiting in the related list will be notified with a simple message and removed from the related list.
	public void removeClientFromQueue (QueueListener client, char type) {
		
		switch (type) {
			case 'A': listenersA.remove(client); break;
			case 'B': listenersB.remove(client); break;
			case 'C': listenersC.remove(client); break;
			case 'D': listenersD.remove(client); break;
			case 'E': listenersE.remove(client); break;
		}
		System.out.println("Client removed from queue for type " + type + " rooms");
	}
	
	private void notifyListeners (char type) {
		
		if (type == 'A') {
			for (QueueListener listenerA: listenersA) {
				try {
					listenerA.roomAvailable("\'A\' type rooms are now available");
				} catch (java.rmi.RemoteException aInE) {
					listenersA.remove(listenerA);
				}
			}
		} else if (type == 'B') {
			for (QueueListener listenerB: listenersB) {
				try {
					listenerB.roomAvailable("\'B\' type rooms are now available");
				} catch (java.rmi.RemoteException aInE) {
					listenersB.remove(listenerB);
				}
			}			
		} else if (type == 'C') {
			for (QueueListener listenerC: listenersC) {
				try {
					listenerC.roomAvailable("\'C\' type rooms are now available");
				} catch (java.rmi.RemoteException aInE) {
					listenersC.remove(listenerC);
				}
			}			
		} else if (type == 'D') {
			for (QueueListener listenerD: listenersD) {
				try {
					listenerD.roomAvailable("\'D\' type rooms are now available");
				} catch (java.rmi.RemoteException aInE) {
					listenersD.remove(listenerD);
				}
			}			
		} else if (type == 'E') {
			for (QueueListener listenerE: listenersE) {
				try {
					listenerE.roomAvailable("\'E\' type rooms are now available");
				} catch (java.rmi.RemoteException aInE) {
					listenersE.remove(listenerE);
				}
			}			
		} 		
	}
 
}