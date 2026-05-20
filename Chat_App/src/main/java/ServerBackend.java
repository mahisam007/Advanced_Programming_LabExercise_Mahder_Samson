import java.io.*;
import java.net.*;
import java.util.*;

public class ServerBackend {

    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());

    public interface MessageListener {
        void onMessageReceived(Message message);
    }

    private MessageListener listener;

    public ServerBackend(MessageListener listener) {
        this.listener = listener;
    }

    public void startServer() {

        new Thread(() -> {

            try (ServerSocket serverSocket =
                         new ServerSocket(5000)) {

                System.out.println("Server Started...");

                while (true) {

                    Socket socket =
                            serverSocket.accept();

                    ClientHandler handler =
                            new ClientHandler(socket);

                    clients.add(handler);

                    new Thread(handler).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    class ClientHandler implements Runnable {

        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {

            this.socket = socket;

            try {

                out =
                        new ObjectOutputStream(
                                socket.getOutputStream()
                        );

                in =
                        new ObjectInputStream(
                                socket.getInputStream()
                        );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {

                while (true) {

                    Message msg =
                            (Message) in.readObject();

                    if (listener != null) {
                        listener.onMessageReceived(msg);
                    }

                    broadcast(msg);
                }

            } catch (Exception e) {
                clients.remove(this);
            }
        }
    }

    public void broadcast(Message msg) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                try {

                    client.out.writeObject(msg);

                    client.out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}