
/**
 * Multithreaded Server
 *
 * @author (Zaid Baalbaki - Rami Dgheim)

 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.sql.*;

public class Server
{
    // instance variables - replace the example below with your own
    private int port= 3350;
    private ServerSocket servSocket;
    public Server() throws ClassNotFoundException 
    {
         Class.forName("com.mysql.jdbc.Driver");
    }
     public void acceptConnections() {

       try {
          servSocket = new ServerSocket(port);
        }
        catch (IOException e) {
          System.err.println("Couldn't start socket");
          e.printStackTrace();
          System.exit(0);
        }
       while (true) {
          try {
     
                 Socket newConnection = servSocket.accept();
                 System.out.println("Connectiion Started");
         
                ServerThread st = new ServerThread(newConnection);
        
                new Thread(st).start();
         }
          catch (IOException ioe) {
            System.err.println("server did not accept");
          }
        }
   }
   public static void main(String args[]) {

   Server server = null;
    try {
      //. This will load the JDBC database driver
      server = new Server();
    }
    catch (ClassNotFoundException e) {
      System.out.println("unable to load JDBC driver");
      e.printStackTrace();
      System.exit(1);
    }

    server.acceptConnections();
  }
  
  
  
  class ServerThread implements Runnable {

    private Socket socket;
    private BufferedReader datain;
    private PrintWriter dataout;

    public ServerThread(Socket socket) {
 
      this.socket = socket;
    }
    
    public void run() {
      try {
        datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataout = new PrintWriter(socket.getOutputStream(), true);
      }
      catch (IOException e) {
        return;
      }
          String username = new String();
          String password = new String();
          String email= new String();
          String logOrReg = new String();
          String welcome = "Hello,Server is active. Login or Register?";
        try{
          while(true){
          dataout.println(welcome);
          dataout.flush();
          logOrReg = datain.readLine();
          if (logOrReg.equalsIgnoreCase("login"))
          {
              
             dataout.println("Enter username: ");
             dataout.flush();
             
             username = datain.readLine();
             while (! userExist(username)){
                dataout.println("Wrong username, please try again. Enter username : ");
                dataout.flush();
                username = datain.readLine();
                }
             dataout.println("Enter password: ");
             dataout.flush();
             password = datain.readLine();
             // check if username and password match 
             while (! correctPass(username,password)){
                dataout.println("Wrong password, please try again. Enter password : ");
                dataout.flush();
                password = datain.readLine();
                }
             boolean cont = true;
             String decision;
             String cnt;
             while ( cont)
             {
             dataout.println("Welcome! What category of our products do you want to view?");
             dataout.flush();
             String Category = datain.readLine();
             dataout.print(viewCategory(Category));
             dataout.flush();
             dataout.println("Please select what you want to add to your cart");
             dataout.flush();
             String product= datain.readLine();
             dataout.println("Please enter quantity");
             dataout.flush();
             cnt =  datain.readLine();
             int count;
             try {
                 count = Integer.parseInt(cnt);
                }
                catch (NumberFormatException e)
                {
                    count = 0;
                }
             additem(username,product,count);
             dataout.println("Item added to cart!Type 'continue' to contiue shopping or 'view' to view cart and checkout.");
             dataout.flush();
             decision = datain.readLine();
             if ( ! decision.equalsIgnoreCase("continue"))
             {cont = false; }
            }
            boolean placed =false;
            while (!placed){
              dataout.println("Here's your cart."+viewcart(username)+" Type 'yes' for checkout.");
              dataout.flush();
            
              String yes = datain.readLine();
              if (yes.equalsIgnoreCase("yes")){
                 dataout.println("Order Placed!");
                 dataout.flush();
                 placed= true;
                }
              else{
              dataout.println("Invalid Input");
               dataout.flush();
               }
             }
           }
          else if (logOrReg.equalsIgnoreCase("register"))
          { dataout.println("Enter username: ");
             dataout.flush();
             username = datain.readLine();
             // Check if username exists
             while ( userExist(username)){
                dataout.println("This username is taken, please try another one. Enter username : ");
                dataout.flush();
                username = datain.readLine();
                }
                
             dataout.println("Enter password: ");
             dataout.flush();
             password = datain.readLine();
             
             dataout.println("Enter email: ");
             dataout.flush();
             email = datain.readLine();
             
             while ( emailExist(email)){
                dataout.println("This email is already used by an existing account. Enter email : ");
                dataout.flush();
                email = datain.readLine();
                }
             // create new row in sql table with corresponding username and password
             newUser(username, password, email);
              dataout.println("Registerd Successfully!");
              dataout.flush();
            }
            else{dataout.println("Not a valid input");
                
            }
        } }
        catch(IOException ioe){}
    
    }

    boolean userExist(String entered)
    {   boolean x = false;
        try{
        Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3308/350","root","");
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select * from users"); // select usernames column
        String usernameCounter;
         while(rs.next()) 
         {
           usernameCounter =  rs.getString("Username");
           if(usernameCounter.equals(entered)) 
           {
               System.out.println("User already exists");
               x = true;
           }
         }

        }catch(Exception e){
            System.out.println(e);
        }
        return x;
    }
    
    boolean emailExist(String entered)
    {   boolean x = false;
        try{
        Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3308/350","root","");
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select * from users"); // select usernames column
        String emailCounter;
         while(rs.next()) 
         {
           emailCounter =  rs.getString("email");
           if(emailCounter.equals(entered)) 
           {
               System.out.println("Email already exists");
               x = true;
           }
         }

        }catch(Exception e){
            System.out.println(e);
        }
        return x;
    }
    boolean correctPass(String userEntered,String passEntered )
    {   boolean x = false;
        try{
        Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3308/350","root","");
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select * from users"); // select users database
        String usernameCounter;
        String passwordCounter;
         while(rs.next()) 
         {
           usernameCounter =  rs.getString("Username");
           passwordCounter = rs.getString("Password");
           if(usernameCounter.equals(userEntered) && passwordCounter.equals(passEntered)) 
           {
               System.out.println(usernameCounter+" Logged In!");
               x = true;
           }
         }

        }catch(Exception e){
            System.out.println(e);
        }
        return x;
    }
    void newUser(String user, String pass, String email)
    {
         try{
             Connection con = DriverManager.getConnection(
             "jdbc:mysql://localhost:3308/350","root","");
       
               // create a Statement from the connection
           PreparedStatement stmt = con.prepareStatement("INSERT INTO users(Username, Password, email) VALUES (?, ?, ?)");

                 stmt.setString(1, user);
                 stmt.setString(2, pass);
                 stmt.setString(3, email);
                    // insert the data
                 stmt.executeUpdate();

             

        }catch(Exception e){
            System.out.println(e);
        }
    }
     String viewCategory(String cat)
    { String result = new String("");
         try{
            
             Connection con = DriverManager.getConnection(
             "jdbc:mysql://localhost:3308/350","root","");
       
               // create a Statement from the connection
              Statement stmt=con.createStatement();
              ResultSet rs=stmt.executeQuery("select * from products Where Category='"+cat+"'");      
              ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                String name = rs.getString("Name");
                String quantity= rs.getString("Quantity");
                String price = rs.getString("Price");
                
                result += "Name: " +name + ", Quantity: " + quantity +
                                   ", Price: " + price+ "\n";
            }

        }catch(Exception e){
            System.out.println(e);
            return "not a valid category";
        }
        return result;
    }
    void additem(String user,String item, int count)
    {   String prc ="";
        try{
         Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3308/350","root","");
        Statement stamt=con.createStatement();
        ResultSet rs=stamt.executeQuery("select * from products Where Name='"+item+"'");
         prc = rs.getString("Price");
       }
       catch(Exception e){
        System.out.println(e);
         } 
        try{ 
        Connection con2 = DriverManager.getConnection(
        "jdbc:mysql://localhost:3308/350","root","");
        
        PreparedStatement stmt = con2.prepareStatement("INSERT INTO carts(User,"+item+ ", Price) VALUES (?, ?, ?)");
        stmt.setString(1, user);
        stmt.setInt(2, count);
        stmt.setInt(3, Integer.parseInt(prc)*count);
        stmt.executeUpdate();
        
        
        }
        catch(Exception e){
        System.out.println(e);
       }
    }
    String viewcart(String user)
    {String result="";
       try{ 
           Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/350","root","");
            Statement stmt=con.createStatement();
              ResultSet rs=stmt.executeQuery("select * from carts Where User='"+user+"'");      
              ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                String name = rs.getString("User");
                String quantity= rs.getString("It");
                String price = rs.getString("Price");
                
                result += "Name: " +name + ", Quantity: " + quantity +
                                   ", Price: " + price+ "\n";
            }

        
        
        }
        catch(Exception e){
        System.out.println(e);
       } 
        return result;
    }
    
}
}