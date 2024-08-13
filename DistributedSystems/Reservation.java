public class Reservation implements java.io.Serializable {
	
	private String name;
	private char type;
	private int numberOfRooms;
	
	public Reservation (String name, char type, int numberOfRooms) {
		this.name = name;
		this.type = type;
		this.numberOfRooms = numberOfRooms;		
	}
	
	public String getName() {
		return name;
	}
	
	public char getType() {
		return type;
	}
	
	public int getNumberOfRooms() {
		return numberOfRooms;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(char type) {
		this.type = type;
	}
	
	public void setNumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}
	
	@Override
    public String toString() {
        return name + ": " + numberOfRooms + " " + type + " rooms.\n";
    }
	
	
}