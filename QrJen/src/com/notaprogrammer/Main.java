package com.notaprogrammer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("QrJen");

        //enter content label
        JLabel enterTextLabel = new JLabel();
        enterTextLabel.setText( "Enter Content Below" );

        //submit button
        JButton button = new JButton("Generate QR Code");

        //textField to enter content
        JTextField contentTextField = new JTextField(50);

        //textField to enter file name
        JTextField fileNameTextField = new JTextField("File Name without extension",16);

        //empty label which will show event after button clicked
        JLabel label1 = new JLabel();

        //add to frame
        frame.add(enterTextLabel);
        frame.add(contentTextField);
        frame.add(fileNameTextField);
        frame.add(button);
        frame.add(label1);

        frame.setSize(700,150);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getRootPane().setDefaultButton(button);
        //action listener lambda
        button.addActionListener(arg0 -> {
            String returnMessage = generateQrCode(contentTextField.getText(), fileNameTextField.getText());
            label1.setText(returnMessage);
        });
    }

    private static String generateQrCode(String content, String fileName){

        String fileType = "png";
        String filePath = getDesktopDirectory() + File.separator + fileName + ".png";

        int size = 250;

        File myFile = new File(filePath);

        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            //Now with zxing version 3.2.1 you could change border size (white border size to just 1)
            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hintMap);

            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();

            BufferedImage image = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            ImageIO.write(image, fileType, myFile);

            Desktop desktop = Desktop.getDesktop();
            desktop.open(myFile);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return "There was a problem creating the QR Code.";
        }

        return "You have successfully created the QR Code.";
    }

    private static String getDesktopDirectory(){
        String OS = (System.getProperty("os.name")).toUpperCase();
        //to determine what the workingDirectory is.
        // if it is some version of Windows
        if (OS.contains("WIN")) {
            return javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().toString();
        }//Otherwise, we assume Linux or Mac
        else {
            return new File(System.getProperty("user.home"), "Desktop").toString();
        }
    }

}
