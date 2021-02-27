
/**

 * @author Zaid Baalbaki - Rami Dghiem 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client
{
    // instance variables - replace the example below with your own
    public static void main(String[] args) {
 
        int port = 3350;
        try (Socket socket = new Socket("localhost",port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            String input = null;
            while (!"Quit".equalsIgnoreCase(input)) {
                System.out.println(in.readLine());
                input = scanner.nextLine();
                out.println(input);
                out.flush();
                
            }
            System.out.println("Good Bye!");
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
