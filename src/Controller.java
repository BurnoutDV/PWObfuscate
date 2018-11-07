import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class Controller {

    private PWColor PWColor = new PWColor();
    private obfuscate hider = new obfuscate();

    @FXML private ToggleButton bt_pwshow;
    @FXML private Button bt_generate;
    @FXML private Slider sl_min_length;
    @FXML private TextField tx_min_length;
    @FXML private Slider sl_max_length;
    @FXML private TextField tx_max_length;
    @FXML private TextField tx_blocksize;
    @FXML private Slider sl_blocksize;
    @FXML private PasswordField tx_password;
    @FXML private TextField tx_unpassword;
    @FXML private ComboBox opt_color;
    private ObservableList<String> opt_colors = FXCollections.observableArrayList();
    @FXML private TextArea tx_html;
    @FXML private Label fx_status;
    @FXML private HTMLEditor tx_htmleditor;
    @FXML private ComboBox opt_pwType;
    private ObservableList<String> opt_passwordType = FXCollections.observableArrayList();
    @FXML private Button fx_colorFileBtn;
    @FXML private TextField fx_colorFilePath;
    @FXML private Button fx_test;

    @FXML
    private void generateHTML(ActionEvent action)
    {
        //CHeck if all input is given, create stuff accordingly
        String password = tx_password.getText();
        int maxLength = (int)Math.abs(sl_max_length.getValue());
        int minLength = (int)Math.abs(sl_min_length.getValue());
        int blocksize = (int)Math.abs(sl_blocksize.getValue());
        if( password.trim().length() > 0)
        {
            fx_status.setText(hider.BuildBlock(password, minLength, maxLength, blocksize));
            //tx_htmleditor.setHtmlText(hider.toHtml(PWColor));
            //htmleditor should update itself
            String colorPref = opt_color.getSelectionModel().getSelectedItem().toString();
            tx_html.setText(hider.toHtml(PWColor, colorPref));
        }
        else {
            tx_html.setText("No Password detected");
            tx_html.setText(opt_color.getSelectionModel().getSelectedItem().toString());
        }
    }

    @FXML
    private void showPW(ActionEvent action)
    {
        System.out.println("showPW triggered");
        //change text of button
        //show pw -> change property text field
        if( tx_password.isVisible() )
        {
            tx_password.setVisible(false); tx_password.setManaged(false);
            tx_unpassword.setVisible(true); tx_unpassword.setManaged(true);
            bt_pwshow.setSelected(true);//should never do anything
        }
        else
        {
            tx_unpassword.setVisible(false); tx_unpassword.setManaged(false);
            tx_password.setVisible(true); tx_password.setManaged(true);
            bt_pwshow.setSelected(false);
        }
    }

    @FXML
    public void testFunction(){
        System.out.println("Test Function Triggered");

    }

    @FXML
    public void initialize() {
        //Slider functionality, i could probably just link them together
        sl_min_length.valueProperty().addListener(((observable, oldValue, newValue) ->
        {//lins Slider and Textfield
            if( oldValue != newValue ) {
                tx_min_length.setText(rooHammer(Double.toString(Math.round(sl_min_length.getValue())),2));
                //rooHammer is just Substring string.length()-2, probably another redundancy
                sl_min_length.setValue(Math.round(sl_min_length.getValue()));
            }
        }));
        sl_max_length.valueProperty().addListener(((observable, oldValue, newValue) ->
        {//lins Slider and Textfield
            if( oldValue != newValue ) {
                tx_max_length.setText(rooHammer(Double.toString(Math.round(sl_max_length.getValue())),2));
                //rooHammer is just Substring string.length()-2, probably another redundancy
                sl_max_length.setValue(Math.round(sl_max_length.getValue()));
            }
        }));
        sl_blocksize.valueProperty().addListener(((observable, oldValue, newValue) ->
        {//lins Slider and Textfield
            if( oldValue != newValue ) {
                tx_blocksize.setText(rooHammer(Double.toString(Math.round(sl_blocksize.getValue())),2));
                //rooHammer is just Substring string.length()-2, probably another redundancy
                sl_blocksize.setValue(Math.round(sl_blocksize.getValue()));
            }
        }));
        //links toogle buttons together
        //https://stackoverflow.com/questions/17014012/how-to-unmask-a-javafx-passwordfield-or-properly-mask-a-textfield
        //apparently it does not work with toogle buttons, too bad
        /*tx_unpassword.managedProperty().bind(bt_pwshow.selectedProperty());
        tx_unpassword.visibleProperty().bind(bt_pwshow.selectedProperty());

        tx_password.managedProperty().bind(bt_pwshow.selectedProperty().not());
        tx_password.visibleProperty().bind(bt_pwshow.selectedProperty().not());*/

        tx_unpassword.setManaged(false);
        tx_unpassword.setVisible(false);
        tx_unpassword.textProperty().bindBidirectional(tx_password.textProperty());

        //hides toolbar of htmleditor

        //Event for text Area changed text
        tx_html.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                // this will run whenever text is changed
                tx_htmleditor.setHtmlText(tx_html.getText());
            }
        });

        //fills color choice and then selects a random one
        LoadColors();
        //prints current time for no particular purpose
        Instant instant = Instant.now();
        System.out.println("CoreController Initialized() ["+instant.toString()+"]");
    }

    public Controller()
    {
        Instant instant = Instant.now();
        System.out.println("CoreController constructed() ["+instant.toString()+"]");
    }

    private void LoadColors()
    {
        if( PWColor.loadXML(".\\config.xml") )
        {
            System.out.println("CoreController.LoadColors(): success");
        }
        else
        {
            PWColor.loadDummyData();
            System.out.println("CoreController.LoadColors(): failure, loading dummy data");
        }
        opt_color.setItems(opt_colors);
        opt_colors.add(lang.STR_RANDOMCOLOR);
        for( Map.Entry<String, String> entry : PWColor.PWColors.entrySet() )
        {
            opt_colors.add(entry.getKey());
        }

        opt_color.getSelectionModel().selectFirst();

    }

    public static String rooHammer(String input, Integer cut)
    {
        //its a hammer cause its one of my tools
        //it cuts two characters without me having to define a variable before
        //there is probably already a function that does that
        if( input.length() >= cut )
        {
            return input.substring(0, input.length() - cut);
        }
        else
        {
            return "";
        }
    }

}
