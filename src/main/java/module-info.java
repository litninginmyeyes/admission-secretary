module org.example.admissionsecretary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires java.mail;


    opens org.example.admissionsecretary to javafx.fxml;
    exports org.example.admissionsecretary;
}