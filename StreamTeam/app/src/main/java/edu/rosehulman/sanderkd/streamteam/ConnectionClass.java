package edu.rosehulman.sanderkd.streamteam;
import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by sanderkd on 1/26/2016.
 */
public class ConnectionClass {
    String ip = "titan.csse.rose-hulman.edu";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "StreamTeam";
    String un = "sanderkd";
    String password = "kicesanders";

    @SuppressLint("NewApi")
    public Connection CONN() {
        Log.d("ConnectionClass", "starting");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.d("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.d("ERRO", e.getMessage());
        }
        Log.d("ConnectionClass", "connected");
        return conn;
    }
}
