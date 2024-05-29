package Backend;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InventoryServer extends JFrame implements Runnable {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private Map<String, DataOutputStream> clientOutputStreams;
    private Map<String, String> clientOTPs = new HashMap<>();
    private JTextArea textArea;
    private int clientNo = 0;
    private Random random = new Random();

    public InventoryServer() {
        super("Inventory Server");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        new Thread(this).start();
    }

    private void initializeComponents() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(9898)) {
            textArea.append("Server started at " + new Date() + '\n');

            clientOutputStreams = new HashMap<>();
            while (true) {
                Socket client = serverSocket.accept();
                clientNo++;
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                clientOutputStreams.put("Client " + clientNo, out);

                // Generate and display OTP for each new connection
                String otp = generateOTP();
                clientOTPs.put("Client " + clientNo, otp);
                textArea.append("Thread started for Client " + clientNo + " at " + new Date() + "\n");
                textArea.append("OTP for Client " + clientNo + ": " + otp + "\n");

                new Thread(new ClientHandler(client, "Client " + clientNo, out, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateOTP() {
        return String.format("%04d", random.nextInt(10000)); // 4-digit OTP
    }

    class ClientHandler implements Runnable {
        private Socket client;
        private String clientName;
        private DataOutputStream out;
        private InventoryServer server;

        public ClientHandler(Socket client, String clientName, DataOutputStream out, InventoryServer server) {
            this.client = client;
            this.clientName = clientName;
            this.out = out;
            this.server = server;
        }

        public void run() {
            try (DataInputStream in = new DataInputStream(client.getInputStream())) {
                while (true) {
                    String received = in.readUTF();
                    if (received.startsWith("OTP:")) {
                        String clientOTP = received.substring(4);
                        if (clientOTP.equals(server.clientOTPs.get(clientName))) {
                            out.writeUTF("Access granted");
                            server.textArea.append(clientName + " has successfully logged in.\n");
                        } else {
                            out.writeUTF("Access denied");
                            server.textArea.append(clientName + " failed login attempt.\n");
                            if (clientNo >0) {
                            	clientNo--;
                            }
                        }
                    } else {
                    	AuditLogger.log(received);
                        SwingUtilities.invokeLater(() -> textArea.append(received + '\n'));
                        broadcast(clientName, received);
                    }
                }
            } catch (IOException e) {
                server.textArea.append(clientName + " disconnected.\n");
                clientOutputStreams.remove(clientName);
            } finally {
                try {
                    client.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void broadcast(String senderName, String message) {
        clientOutputStreams.forEach((name, output) -> {
            if (!name.equals(senderName)) {
                try {
                    output.writeUTF(message);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        new InventoryServer();
    }
}
