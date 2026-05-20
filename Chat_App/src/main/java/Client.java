import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Client(String host, int port) {

        try {

            socket = new Socket(host, port);

            System.out.println("[CLIENT] Connected to server.");

            output =
                    new ObjectOutputStream(
                            socket.getOutputStream()
                    );

            input =
                    new ObjectInputStream(
                            socket.getInputStream()
                    );

        } catch (IOException e) {

            System.out.println(
                    "[CLIENT ERROR] Could not connect."
            );

            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {

        try {

            output.writeObject(message);

            output.flush();

            if (message.isImage()) {

                System.out.println(
                        "[CLIENT] Image sent."
                );

            } else {

                System.out.println(
                        "[CLIENT] Message sent: "
                                + message.getText()
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage() {

        try {

            Message msg =
                    (Message) input.readObject();

            if (msg.isImage()) {

                System.out.println(
                        "[CLIENT] Image received."
                );

            } else {

                System.out.println(
                        "[CLIENT] Message received: "
                                + msg.getText()
                );
            }

            return msg;

        } catch (Exception e) {

            System.out.println(
                    "[CLIENT] Connection closed."
            );

            return null;
        }
    }
}