package server;
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 80; // 默认HTTP端口
        String rootDirectory = "/path/to/your/root/directory"; // 设置你的Web根目录

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Web服务器已启动，监听端口: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 等待客户端连接
                handleClientRequest(clientSocket, rootDirectory);
            }
        } catch (IOException e) {
            System.err.println("Web服务器启动失败: " + e.getMessage());
        }
    }

    /**
     * 处理客户端请求
     * @param clientSocket
     * @param rootDirectory
     */
    private static void handleClientRequest(Socket clientSocket, String rootDirectory) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            // 读取HTTP请求头部
            String requestLine = in.readLine();
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length == 3) {
                    String method = requestParts[0];
                    String path = requestParts[1];

                    if (method.equals("GET")) {
                        // 构建请求的文件路径
                        String filePath = rootDirectory + path;

                        File file = new File(filePath);
                        if (file.exists() && !file.isDirectory()) {
                            // 发送HTTP响应头部
                            out.writeBytes("HTTP/1.1 200 OK\r\n");
                            out.writeBytes("Content-Length: " + file.length() + "\r\n");
                            out.writeBytes("\r\n");

                            // 发送文件内容
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            fileInputStream.close();
                        } else {
                            // 文件不存在，发送404响应
                            out.writeBytes("HTTP/1.1 404 Not Found\r\n");
                            out.writeBytes("\r\n");
                        }
                    }
                }
            }

            // 关闭连接
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("处理请求时发生错误: " + e.getMessage());
        }
    }
}
