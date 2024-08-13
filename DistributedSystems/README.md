## A simple hotel rooms reservation program using Java RMI

 Multiple clients can connect to the hotel reservations server and do the following (by inserting the appropriate arguments):
- See a list of all available rooms (number, category & price).
- See a list of all hotel guests each time
- Book one or more rooms of a specific category. If the book is successfull, clients will receive a message with the total cost. 
Otherwise (if all rooms of that category are reserved) they can choose to be on a waiting list for the deserved category until one or more will be released.
- Cancel one or more reserved rooms.

By running the client program with no arguments, the user can see the application's manual.