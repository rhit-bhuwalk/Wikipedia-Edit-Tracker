import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
	
	private Connection connection = null;
	
    public SQLConnection(String name, String password) {
    	String templateUrl = "jdbc:sqlserver://${dbServer};database=${dbName};user=${user};password=${pass}";
        String connectionUrl = templateUrl
        		.replace("${dbServer}", "titan.csse.rose-hulman.edu")
        		.replace("${dbName}", "WikipediaEditsTracker")
        		.replace("${user}", name)
        		.replace("${pass}", password);

        try  { 
        	connection = DriverManager.getConnection(connectionUrl);
            System.out.println("Successful Connection");
        }
        
        catch (SQLException e) {
        }
    }
    
	public Connection getConnection() {
		return this.connection;
	}
	
	public void closeConnection() {
		try {
			connection.close();
			System.out.println("Connection Closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}