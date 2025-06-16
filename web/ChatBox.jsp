<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>ChatBox</title>
    <style>
        .chatbox {
            width: 350px;
            height: 450px;
            border: 2px solid #007bff;
            margin: 20px auto;
            display: flex;
            flex-direction: column;
            background: #f0f4f8;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .messages {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
            background: #ffffff;
            border-bottom: 1px solid #ddd;
        }
        .input-area {
            padding: 10px;
            background: #e9ecef;
            border-radius: 0 0 10px 10px;
        }
        .input-area input {
            width: 75%;
            padding: 8px;
            border: 1px solid #ced4da;
            border-radius: 5px;
        }
        .input-area button {
            padding: 8px 15px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .input-area button:hover {
            background: #0056b3;
        }
        .message {
            margin: 10px 0;
            padding: 10px;
            border-radius: 8px;
            max-width: 70%;
        }
        .sent { background: #d1e7ff; text-align: right; }
        .received { background: #fff3cd; text-align: left; }
        .logo {
            text-align: center;
            padding: 10px;
        }
        .logo img {
            width: 50px;
            height: auto;
        }
        .preferences {
            font-style: italic;
            color: #555;
            text-align: center;
            padding: 5px;
        }
    </style>
</head>
<body>
    <div class="chatbox">
        <div class="logo">
            <img src="https://via.placeholder.com/50" alt="Chat Logo">
        </div>
        <div class="messages" id="messageArea">
            <% 
                Map<String, String> chatHistory = (Map<String, String>) session.getAttribute("chatHistory");
                if (chatHistory != null) {
                    for (Map.Entry<String, String> entry : chatHistory.entrySet()) {
                        String userMsg = entry.getKey();
                        String agentMsg = entry.getValue();
                        if (userMsg.startsWith("user:")) {
                            out.println("<div class='message sent'>" + userMsg.substring(5) + "</div>");
                        }
                        if (agentMsg.startsWith("agent:")) {
                            out.println("<div class='message received'>" + agentMsg.substring(6) + "</div>");
                        }
                    }
                }
                String responseText = (String) request.getAttribute("responseText");
                if (responseText != null) {
                    out.println("<div class='message received'>" + responseText + "</div>");
                }
            %>
        </div>
        <div class="input-area">
            <input type="text" id="messageInput" placeholder="Nhập tin nhắn...">
            <button onclick="sendMessage()">💬 Gửi</button>
        </div>
    </div>
    <% String preferences = (String) request.getAttribute("preferences"); %>
    <% if (preferences != null) { %>
        <div class="preferences"><%= preferences %></div>
    <% } %>

    <script>
        function sendMessage() {
            const input = document.getElementById("messageInput");
            const message = input.value.trim();
            if (message) {
                const messageArea = document.getElementById("messageArea");
                const userMessage = document.createElement("div");
                userMessage.className = "message sent";
                userMessage.textContent = message;
                messageArea.appendChild(userMessage);
                messageArea.scrollTop = messageArea.scrollHeight;

                // Gửi tin nhắn tới servlet
                fetch('/techchat', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'query=' + encodeURIComponent(message)
                })
                .then(response => response.text())
                .then(data => {
                    const agentMessage = document.createElement("div");
                    agentMessage.className = "message received";
                    agentMessage.textContent = data;
                    messageArea.appendChild(agentMessage);
                    messageArea.scrollTop = messageArea.scrollHeight;
                })
                .catch(error => console.error('Error:', error));

                input.value = "";
            }
        }

        document.getElementById("messageInput").addEventListener("keypress", function(e) {
            if (e.key === "Enter") sendMessage();
        });
    </script>
</body>
</html>