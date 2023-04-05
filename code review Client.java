import java.io.BufferedReader; // Importing the BufferedReader class from the java.io package
import java.io.IOException; // Importing the IOException class from the java.io package
import java.io.InputStreamReader; // Importing the InputStreamReader class from the java.io package
import java.io.PrintWriter; // Importing the PrintWriter class from the java.io package
import java.net.Socket; // Importing the Socket class from the java.net package

public class Client implements Runnable { // Defining a public class named "Client" that implements the Runnable interface

    private Socket client; // Declaring a private instance variable of type Socket named "client"
    private BufferedReader in; // Declaring a private instance variable of type BufferedReader named "in"
    private PrintWriter out; // Declaring a private instance variable of type PrintWriter named "out"
    private boolean done; // Declaring a private instance variable of type boolean named "done"

    @Override // Annotation indicating that the following method overrides a method of the superclass or interface
    public void run() { // Defining the implementation of the "run" method inherited from the Runnable interface
        try { // Starting a try block to catch any IOExceptions that may occur
            client = new Socket("127.0.0.1", 9999); // Creating a new Socket instance with the IP address and port number specified
            out = new PrintWriter(client.getOutputStream(), true); // Creating a new PrintWriter instance that will send data through the socket's output stream
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Creating a new BufferedReader instance that will read data from the socket's input stream
            InputHandler inHandler = new InputHandler(); // Creating a new instance of the InputHandler class
            Thread t = new Thread(inHandler); // Creating a new Thread instance and passing the InputHandler instance as a parameter
            t.start(); // Starting the new thread
            String inMessage; // Declaring a new String variable named "inMessage"
            while ((inMessage = in.readLine()) != null) { // Starting a loop that reads data from the input stream and assigns it to the "inMessage" variable until the end of the stream is reached
                System.out.println(inMessage); // Printing the contents of the "inMessage" variable to the console
            }
        } catch (IOException e) { // Catching any IOExceptions that may occur
            shutdown(); // Calling the "shutdown" method to close the socket and streams
        }
    }

    public void shutdown() { // Defining a public method named "shutdown"
        done = true; // Setting the "done" variable to true
        try { // Starting a try block to catch any IOExceptions that may occur
            in.close(); // Closing the input stream
            out.close(); // Closing the output stream
            if (!client.isClosed()) { // Checking if the socket is not already closed
                client.close(); // Closing the socket
            }

        } catch (IOException e) { // Catching any IOExceptions that may occur
            //ignore - not doing anything with the exception
        }
    }

    class InputHandler implements Runnable { // Defining a private inner class named "InputHandler" that implements the Runnable interface

        @Override // Annotation indicating that the following method overrides a method of the superclass or interface
        public void run() { // Defining the implementation of the "run" method inherited from the Runnable interface
            try { // Starting a try block to catch any IOExceptions that may occur
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in)); // Creating a new BufferedReader instance that will read data from the console input
                while (!done) { // Starting a loop that runs until the "done" variable is set to true
                    String message = inReader.readLine(); // Reading a line of input from the console and assigning it to the "message" variable
                    if (message.equals("/quit")) { // Checking if the "message" variable contains the "/quit" command
                        out.println(message); // Sending the "/quit" command to the server through the socket's output stream
                        inReader.close(); // Closing the console input stream
                        shutdown(); // Calling the "shutdown" method to close the socket and streams
                    } else {
                        out.println(message); // Sending the contents of the "message" variable to the server through the socket's output stream
                    }
                }

            } catch (IOException e) { // Catching any IOExceptions that may occur
                shutdown(); // Calling the "shutdown" method to close the socket and streams
            }
        }
    }

    public static void main(String[] args) { // Defining the main method of the program
        Client client = new Client(); // Creating a new instance of the Client class
        client.run(); // Calling the "run" method of the Client instance
    }
}
