package crypto.project.GUI.MainMenu;

import crypto.project.Model.castTypes.TypeConverter;
import crypto.project.Model.crypto.DesX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class Menu implements Initializable {

    private final DesX desX = new DesX();

    @FXML //general variables
    public TextArea normalText = new TextArea();
    public TextArea codedText = new TextArea();
    public Button onGenerate;
    public RadioButton fileicon = new RadioButton();
    public RadioButton texticon = new RadioButton();
    public TextArea keyText2;
    public TextArea keyText3;
    public TextArea keyText1;

    @FXML //file system support
    public TextField uploadNormalFile;
    public Button chooseNormalFile;
    public Button SaveCodedFile;
    public TextField saveCodedFile;
    public Button SaveNormalFile;
    public TextField saveNormalFile;
    public TextField uploadCodedFile;
    public Button chooseCodedFile;

    private File normalFileBuffer;
    private byte[] normalFileInByteForm;

    private File codedFileBuffor;
    private byte[] codedFileInByteForm;


    public void onCode(ActionEvent actionEvent) {
        try {
            byte[] firstXorKey = TypeConverter.stringToByteTab(keyText1.getText());
            byte[] desKey = TypeConverter.stringToByteTab(keyText2.getText());
            byte[] secondXorKey = TypeConverter.stringToByteTab(keyText3.getText());

            byte[] textAreaInByteForm = null;

            if(texticon.isSelected()) { //select the appropriate buffer for analysis
                textAreaInByteForm = TypeConverter.stringToByteTab(normalText.getText());
                byte[] text = Base64.getEncoder().encode(desX.codeText(textAreaInByteForm,firstXorKey,desKey,secondXorKey));
                codedText.setText(new String(text));
            } else {
                textAreaInByteForm = normalFileInByteForm;
                codedFileInByteForm = desX.codeText(textAreaInByteForm,firstXorKey,desKey,secondXorKey);
                byte[] text = Base64.getEncoder().encode(codedFileInByteForm);
                codedText.setText(new String(text));
            }
        } catch (Exception e) {
            try {
                Error error = new Error();
                error.show();
            } catch (Exception ignored) {
            }
        }
    }

    public void onDeCode(ActionEvent actionEvent) throws IOException {
        try {
            byte[] firstXorKey = TypeConverter.stringToByteTab(keyText1.getText());
            byte[] desKey = TypeConverter.stringToByteTab(keyText2.getText());
            byte[] secondXorKey = TypeConverter.stringToByteTab(keyText3.getText());

            byte[] textAreaInByteForm = null;

            if(texticon.isSelected()) { //select the appropriate buffer for analysis
                byte[] temp = Base64.getDecoder().decode(codedText.getText());
                byte[] text = desX.codeText(temp,firstXorKey,desKey,secondXorKey);
                normalText.setText(TypeConverter.byteTabToString(text));
            } else {
                textAreaInByteForm = codedFileInByteForm;
                normalFileInByteForm = desX.codeText(textAreaInByteForm,firstXorKey,desKey,secondXorKey);
                byte[] text = Base64.getEncoder().encode(normalFileInByteForm);
                normalText.setText(TypeConverter.byteTabToString(text));
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                Error error = new Error();
                error.show();
            } catch (Exception ex) {

            }
        }
    }

    public void show() throws IOException {
        Stage menuStage = new Stage();
        menuStage.setResizable(false);
        menuStage.setTitle("Crypto");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("MainMenu.fxml")));
        Scene scene = new Scene(root);
        menuStage.setResizable(false);
        menuStage.setScene(scene);
        menuStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        normalText.setWrapText(true);
        codedText.setWrapText(true);

        texticon.setSelected(true);
    }

    private String generateKey(int length) throws UnsupportedEncodingException {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void genFirstKey() throws UnsupportedEncodingException {
        keyText1.setText(generateKey(8));
    }

    private void genSecondKey() throws UnsupportedEncodingException {
        keyText2.setText(generateKey(7));
    }

    private void genThirdKey() throws UnsupportedEncodingException {
        keyText3.setText(generateKey(8));
    }

    public void onGenerate(ActionEvent actionEvent) throws UnsupportedEncodingException {
        genFirstKey();
        genSecondKey();
        genThirdKey();
    }

    public void onFIleChooose(ActionEvent actionEvent) {
        fileicon.setSelected(true);
        texticon.setSelected(false);
    }

    public void onTextChoose(ActionEvent actionEvent) {
        texticon.setSelected(true);
        fileicon.setSelected(false);
    }


    public void onNormalFileChoose(ActionEvent actionEvent) {
        try {
            Stage normalFileStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            normalFileBuffer = fileChooser.showOpenDialog(normalFileStage);
            uploadNormalFile.setText(normalFileBuffer.getAbsolutePath());
            normalFileInByteForm = Files.readAllBytes(Path.of(normalFileBuffer.getAbsolutePath()));
            String s = new String(normalFileInByteForm, System.getProperty("file.encoding"));
            normalText.setText(s);
        } catch (Exception ignored) {
        }
    }

    public void onSaveCodedFile(ActionEvent actionEvent) {
        if(texticon.isSelected()) { //save coded text as a file(from textField)
            try {
                Stage saveTextAsFile = new Stage();
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Save Text as a file");
                String directory = "";
                directory = directoryChooser.showDialog(saveTextAsFile).toString();

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(directory+"\\"+saveCodedFile.getText()));
                    writer.write(codedText.getText());
                    writer.close();
                    saveCodedFile.clear();
                } catch (Exception e) {
                    Error error = new Error();
                    error.show();
                }
            } catch (Exception ignored) {
            }

        } else { //save coded bytes from var as a file
            try {
                Stage saveFileStage = new Stage();
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Save file");
                String directory = "";
                directory = directoryChooser.showDialog(saveFileStage).toString();
                try (FileOutputStream fos = new FileOutputStream(directory+"\\"+saveCodedFile.getText())) {
                    fos.write(codedFileInByteForm);
                    fos.close();
                    saveCodedFile.clear();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    Error error = new Error();
                    error.show();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void onSaveFileChoose(ActionEvent actionEvent) {
        if(texticon.isSelected()) {
            try {
                Stage saveNormalTextAsFile = new Stage();
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Save Text as a file");
                String directory = "";
                directory = directoryChooser.showDialog(saveNormalTextAsFile).toString();

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(directory+"\\"+saveNormalFile.getText()));
                    writer.write(normalText.getText());
                    writer.close();
                    saveNormalFile.clear();
                } catch (Exception e) {
                    Error error = new Error();
                    error.show();
                }
            } catch (Exception ignored) {
            }
        } else {
            try {
                Stage saveNormalFileStage = new Stage();
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Save file");
                String directory = "";
                directory = directoryChooser.showDialog(saveNormalFileStage).toString();
                try (FileOutputStream fos = new FileOutputStream(directory+"\\"+saveNormalFile.getText())) {
                    fos.write(normalFileInByteForm);
                    fos.close();
                    saveNormalFile.clear();
                } catch (Exception ignored) {
                    Error error = new Error();
                    error.show();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void onchooseCodedFile(ActionEvent actionEvent) {
        try {
            Stage codedFileChoose = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Coded File");
            codedFileBuffor = fileChooser.showOpenDialog(codedFileChoose);
            uploadCodedFile.setText(codedFileBuffor.getAbsolutePath());
            codedFileInByteForm = Files.readAllBytes(Path.of(codedFileBuffor.getAbsolutePath()));
            String s = new String(codedFileInByteForm, System.getProperty("file.encoding"));
            codedText.setText(s);
        } catch (Exception ignored) {
        }
    }
}
