package com.mycompany.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.persistence.Persistence;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

@interface MyThingy {
    String cool();

    int awesome();
}

@MyThingy(awesome = 0, cool = "foo")
public class App {
    static void readConfig1() throws FileNotFoundException, IOException {
        System.out.println("reading the config file...");
        FileInputStream configFile = new FileInputStream("src/main/resources/my-config.properties");
        Properties config = new Properties();
        config.load(configFile);
        System.out.println(config);
        System.out.println(System.getProperty("user.dir"));
        configFile.close();
    }

    static void readConfig2() {
        System.out.println("reading the config file using ResourceBundle...");
        ResourceBundle config = ResourceBundle.getBundle("com.mycompany.app.my-config");
        System.out.println(config);
        for (var e = config.getKeys(); e.hasMoreElements();) {
            String key = e.nextElement();
            System.out.printf("%s %s\n", key, config.getString(key));
        }
    }

    static void readConfig3() throws IOException {
        System.out.println("reading the config file using class loader...");
        InputStream configFile = App.class.getResourceAsStream("my-config.properties");
        if (configFile == null) {
            System.out.println("failed to read the config file");
            return;
        }
        Properties config = new Properties();
        config.load(configFile);
        System.out.println(config);
        System.out.println(System.getProperty("user.dir"));
        configFile.close();
    }

    static void doStuffWithDB() throws SQLException, ClassNotFoundException {
        System.out.println("connecting to db...");
        String dbURL = "jdbc:postgresql://localhost:5432/";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "mypass1234");
        Connection conn = DriverManager.getConnection(dbURL, props);
        // PreparedStatement stmt = conn.prepareStatement("CREATE TABLE customers (" +
        // "id int," +
        // "name varchar(255)," +
        // "age int," +
        // "address varchar(255)," +
        // "PRIMARY KEY (id)" +
        // ")");
        // PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(id,
        // name, age, address) VALUES(0, 'customer 1', 20, '42nd Street, Long Beach')");
        // PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(id,
        // name, age, address) VALUES(1, 'customer 2', 24, '43rd Street, Long Beach')");
        // PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(id,
        // name, age, address) VALUES(2, 'customer 3', 18, '22nd Jump Street, Long
        // Beach')");
        // PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers");
        // PreparedStatement stmt = conn.prepareStatement("DROP TABLE customers");
        // PreparedStatement stmt = conn
        // .prepareStatement("INSERT INTO customers(id, name, age, address) VALUES(?, ?,
        // ?, ?)");
        // Customer[] customers = {
        // new Customer(0, "customer 1", 20, "42nd Street, Long Beach"),
        // new Customer(1, "customer 2", 24, "43rd Street, Long Beach"),
        // new Customer(2, "customer 3", 18, "22nd Jump Street, Long Beach"),
        // };
        // for (Customer customer : customers) {
        // stmt.setInt(1, customer.id);
        // stmt.setString(2, customer.name);
        // stmt.setInt(3, customer.age);
        // stmt.setString(4, customer.address);
        // if (stmt.execute()) {
        // System.out.println("result set");
        // printResultSet(stmt.getResultSet());
        // } else {
        // System.out.println("update count");
        // System.out.println(stmt.getUpdateCount());
        // }
        // }
        // PreparedStatement stmt = conn
        // .prepareStatement(
        // "UPDATE customers SET age = 22, address = '42nd avenue, New York City, New
        // York' WHERE id = 2");
        PreparedStatement stmt = conn
                .prepareStatement(
                        "SELECT * FROM pg_catalog.pg_tables");
        // PreparedStatement stmt = conn
        // .prepareStatement(
        // "DELETE FROM customers WHERE id = 1");
        // PreparedStatement stmt = conn.prepareStatement("DROP TABLE customers");
        if (stmt.execute()) {
            System.out.println("result set");
            printResultSet(stmt.getResultSet());
        } else {
            System.out.println("update count");
            System.out.println(stmt.getUpdateCount());
        }
        conn.close();
    }

    static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    static void doJPAStuff() throws InterruptedException {
        Customer customer = new Customer(1, "customer 1", 20, "abc beach");
        // CustomerDao dao = new CustomerDao();
        // dao.save(customer);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CustomerManagement");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(customer);
        System.out.println("sleeping");
        for (int i = 3; i >= 0; i--) {
            TimeUnit.SECONDS.sleep(1);
            System.out.printf("%d..., ", i);
        }
        System.out.println("woke up");
        Customer foundCustomer = em.find(Customer.class, 1);
        if (foundCustomer == null) {
            System.out.println("failed to find the customer with id 1");
        } else {
            System.out.println("found the customer with id 1");
            System.out.println(foundCustomer);
        }
        customer.setAge(22);
        customer.setAddress("def island");
        System.out.println("after udating customer");
        System.out.println(customer);
        System.out.println("after udating, the found customer is");
        System.out.println(foundCustomer);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    static void doStuffWithH2EmbeddedDB() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        System.out.println("connecting to H2 embedded db...");
        // String dbURL = "jdbc:h2:~/mytesth2db"; // write database to a file in home directory ~/mytesth2db.mv.db and errors to ~/mytesth2db.trace.db
        String dbURL = "jdbc:h2:mem:mytesth2db"; // https://www.h2database.com/html/features.html#in_memory_databases
        Properties props = new Properties();
        // props.setProperty("user", "postgres");
        // props.setProperty("password", "mypass1234");
        Connection conn = DriverManager.getConnection(dbURL, props);
        // PreparedStatement stmt = conn
        //         .prepareStatement(
        //                 "SELECT * FROM pg_catalog.pg_tables");
        // PreparedStatement stmt = conn
        // .prepareStatement(
        // "DELETE FROM customers WHERE id = 1");
        // PreparedStatement stmt = conn.prepareStatement("DROP TABLE customers");
        {
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE customers (" +
            "id int," +
            "name varchar(255)," +
            "age int," +
            "address varchar(255)," +
            "PRIMARY KEY (id)" +
            ")");
            if (stmt.execute()) {
                System.out.println("result set");
                printResultSet(stmt.getResultSet());
            } else {
                System.out.println("update count");
                System.out.println(stmt.getUpdateCount());
            }
        }
        {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(id, name, age, address) VALUES(0, 'customer 1', 20, '42nd Street, Long Beach')");
            if (stmt.execute()) {
                System.out.println("result set");
                printResultSet(stmt.getResultSet());
            } else {
                System.out.println("update count");
                System.out.println(stmt.getUpdateCount());
            }    
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers");
            if (stmt.execute()) {
                System.out.println("result set");
                printResultSet(stmt.getResultSet());
            } else {
                System.out.println("update count");
                System.out.println(stmt.getUpdateCount());
            }
        }
        conn.close();
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, InterruptedException {
        // readConfig1();
        // readConfig2();
        // readConfig3();
        // doStuffWithDB();
        // doJPAStuff();
        doStuffWithH2EmbeddedDB();
        System.out.println("done");
    }
}
