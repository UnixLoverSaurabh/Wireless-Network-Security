package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5700);

        try {
            System.out.println("Enter your name");
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            String username = scanner.nextLine();
            objectOutputStream.writeObject("Username : " + username);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            SecretKey secretKey = (SecretKey)objectInputStream.readObject();
            System.out.println("Common AES key : " + secretKey);


            System.out.println("Enter the message");
            String mess = "Hii";
            MessageEncryption messageEncryption;

            while(!mess.equals("over")) {
                mess = scanner.nextLine();

                if(mess.equals("file")) {
                    objectOutputStream.writeObject("File");
                    objectOutputStream.flush();

                    File file = new File("trigger.sql");
                    byte [] fileArray  = Files.readAllBytes(file.toPath());
                    messageEncryption = new MessageEncryption(fileArray, secretKey);
                    objectOutputStream.writeObject(messageEncryption.getCipherText());
                    objectOutputStream.flush();
                }
                else {
                    objectOutputStream.writeObject("String");
                    objectOutputStream.flush();

                    messageEncryption = new MessageEncryption(mess, secretKey);
                    objectOutputStream.writeObject(messageEncryption.getCipherText());
                    objectOutputStream.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in MessageEncryption(mess, secretKey)");
            e.printStackTrace();
        } catch (InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
