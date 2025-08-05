import java.io.InputStream;
import java.net.*;

public class PostgresProxyTest {
    public static void main(String[] args) {
        String host = "yourserver.postgres.database.azure.com";
        int port = 5432;

        SocketAddress proxyAddr = new InetSocketAddress("localhost", 1080); // SOCKS proxy
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);

        try (Socket socket = new Socket(proxy)) {
            socket.connect(new InetSocketAddress(host, port));
            System.out.println("✅ Connection successful!");

            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);

            if (bytesRead == -1) {
                System.out.println("📭 Server closed the connection without sending any data.");
            } else {
                System.out.println("📥 Received " + bytesRead + " bytes from server:");
                for (int i = 0; i < bytesRead; i++) {
                    System.out.printf("%02X ", buffer[i]);
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
