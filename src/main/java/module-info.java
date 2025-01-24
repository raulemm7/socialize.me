module org.example.labx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jgrapht.core;
    requires jdk.jshell;
    requires jdk.incubator.foreign;
    requires org.postgresql.jdbc;
    requires java.management;


    opens org.example.labx to javafx.fxml;
    exports org.example.labx;
    opens org.example.labx.repository to javafx.fxml;
}