module myposv2 {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    //Database
    requires java.sql;
    
    exports mypos;
    exports models;
    exports app;
}
