module com.vhaa.translatorrfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.vhaa.translatorrfx to javafx.fxml;
    exports com.vhaa.translatorrfx;
}