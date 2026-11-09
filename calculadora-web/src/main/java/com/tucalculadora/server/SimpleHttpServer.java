package com.tucalculadora.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.tucalculadora.nodos.NodoExpresion;
import com.tucalculadora.parser.ParseError;
import com.tucalculadora.parser.ParserInterno;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class SimpleHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/derivar", new DerivadaHandler());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("✅ Servidor corriendo en http://localhost:8080");
        System.out.println("📁 Abre http://localhost:8080");
    }

    static class DerivadaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                enviarRespuesta(exchange, 204, "");
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                enviarRespuesta(exchange, 405, "{\"error\": \"Método no permitido\"}");
                return;
            }

            String body = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
            ).lines().reduce("", (a, b) -> a + b);

            String funcion = extraerFuncion(body);

            if (funcion == null || funcion.isEmpty()) {
                enviarRespuesta(exchange, 400, "{\"error\": \"Falta el campo 'funcion'\"}");
                return;
            }

            try {
                ParserInterno parser = new ParserInterno(funcion);
                NodoExpresion arbol = parser.expressionTree();

                NodoExpresion derivada = arbol.derivar();
                NodoExpresion simplificada = derivada.simplificar();

                String fLaTeX = arbol.toLaTeX();
                String fPrimaLaTeX = simplificada.toLaTeX();

                String respuesta = String.format(
                    "{\"funcion\": \"%s\", \"derivada\": \"%s\", \"exito\": true}",
                    fLaTeX.replace("\"", "\\\""),
                    fPrimaLaTeX.replace("\"", "\\\"")
                );

                enviarRespuesta(exchange, 200, respuesta);

            } catch (ParseError e) {
                enviarRespuesta(exchange, 400, String.format(
                    "{\"error\": \"%s\"}", e.getMessage()
                ));
            } catch (Exception e) {
                enviarRespuesta(exchange, 500, String.format(
                    "{\"error\": \"%s\"}", e.getMessage()
                ));
            }
        }

        private String extraerFuncion(String json) {
            String key = "\"funcion\"";
            int idx = json.indexOf(key);
            if (idx == -1) return null;

            int start = json.indexOf(":", idx) + 1;
            while (start < json.length() && json.charAt(start) == ' ') start++;

            if (json.charAt(start) != '"') return null;
            start++;

            int end = start;
            while (end < json.length() && json.charAt(end) != '"') end++;

            return json.substring(start, end);
        }

        private void enviarRespuesta(HttpExchange exchange, int status, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            String filePath = "src/frontend" + path;
            File file = new File(filePath);

            if (!file.exists()) {
                enviarRespuesta(exchange, 404, "{\"error\": \"Archivo no encontrado\"}");
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html";
            else if (path.endsWith(".css")) contentType = "text/css";
            else if (path.endsWith(".js")) contentType = "application/javascript";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }

        private void enviarRespuesta(HttpExchange exchange, int status, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}