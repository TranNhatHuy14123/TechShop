<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tech Chat</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        /* Chat bubble icon */
        .chat-toggle {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background-color: #ff5722;
            color: white;
            padding: 12px 18px;
            border-radius: 30px;
            cursor: pointer;
            font-weight: bold;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
            z-index: 999;
        }

        /* Chatbox container */
        .chatbox {
            display: none;
            flex-direction: column;
            width: 350px;
            height: 450px;
            position: fixed;
            bottom: 70px;
            right: 20px;
            border: 2px solid #007bff;
            background: #f0f4f8;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            z-index: 1000;
        }

        .logo {
            text-align: center;
            padding: 10px;
            position: relative;
        }

        .logo img {
            width: 40px;
            height: auto;
        }

        /* Close button */
        .close-chat {
            position: absolute;
            right: 10px;
            top: 10px;
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
            color: #333;
        }

        .messages {
            flex: 1;
            overflow-y: auto;
            padding: 10px;
            background: #ffffff;
            border-top: 1px solid #ddd;
            border-bottom: 1px solid #ddd;
        }

        .message {
            margin: 8px 0;
            padding: 8px;
            border-radius: 8px;
            max-width: 80%;
            word-wrap: break-word;
        }

        .sent {
            background-color: #d1e7ff;
            align-self: flex-end;
            text-align: right;
        }

        .received {
            background-color: #fff3cd;
            align-self: flex-start;
            text-align: left;
        }

        .input-area {
            padding: 10px;
            display: flex;
            background-color: #e9ecef;
            border-radius: 0 0 10px 10px;
        }

        .input-area input {
            flex: 1;
            padding: 8px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            margin-right: 5px;
        }

        .input-area button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
        }

        .input-area button:hover {
            background-color: #0056b3;
        }

        .preferences {
            font-style: italic;
            color: #333;
            font-size: 12px;
            padding: 5px 15px;
            text-align: center;
        }
    </style>
</head>
<body>

<!-- Chat toggle icon -->
<div class="chat-toggle" id="chatToggle" onclick="openChat()">💬 Chat</div>

<!-- Chatbox -->
<div class="chatbox" id="chatBox">
    <div class="logo">
        <img src="https://thumbs.dreamstime.com/b/chat-icon-isolated-white-background-79426494.jpg?w=768" alt="Chat Icon">
        <button class="close-chat" onclick="closeChat()">×</button>
    </div>
    <div class="messages" id="messageArea">
        <%
            Map<String, String> chatHistory = (Map<String, String>) session.getAttribute("chatHistory");
            if (chatHistory != null) {
                for (Map.Entry<String, String> entry : chatHistory.entrySet()) {
                    String userMsg = entry.getKey().replaceFirst("user:", "").trim();
                    String agentMsg = entry.getValue().replaceFirst("agent:", "").trim();
        %>
                    <div class="message sent"><%= userMsg %></div>
                    <div class="message received"><%= agentMsg %></div>
        <%
                }
            }
        %>
    </div>
    <div class="input-area">
        <input type="text" id="messageInput" placeholder="Nhập tin nhắn..." />
        <button onclick="sendMessage()">Gửi</button>
    </div>
</div>

<%
    String preferences = (String) request.getAttribute("preferences");
    if (preferences != null) {
%>
    <div class="preferences"><%= preferences %></div>
<%
    }
%>

<script>
    function openChat() {
        document.getElementById("chatBox").style.display = "flex";
        document.getElementById("chatToggle").style.display = "none";
    }

    function closeChat() {
        document.getElementById("chatBox").style.display = "none";
        document.getElementById("chatToggle").style.display = "block";
    }

    function sendMessage() {
        const input = document.getElementById("messageInput");
        const message = input.value.trim();
        if (!message) return;

        const messageArea = document.getElementById("messageArea");

        const userMsgDiv = document.createElement("div");
        userMsgDiv.className = "message sent";
        userMsgDiv.textContent = message;
        messageArea.appendChild(userMsgDiv);
        messageArea.scrollTop = messageArea.scrollHeight;

        fetch('<%= request.getContextPath() %>/techchat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'query=' + encodeURIComponent(message)
        })
        .then(response => response.text())
        .then(data => {
            const botMsgDiv = document.createElement("div");
            botMsgDiv.className = "message received";
            botMsgDiv.textContent = data;
            messageArea.appendChild(botMsgDiv);
            messageArea.scrollTop = messageArea.scrollHeight;
        })
        .catch(error => console.error("Gửi thất bại:", error));

        input.value = "";
    }

    document.getElementById("messageInput").addEventListener("keypress", function (e) {
        if (e.key === "Enter") sendMessage();
    });
</script>

</body>
</html>
