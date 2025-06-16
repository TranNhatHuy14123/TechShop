package agents;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TechChatServlet extends HttpServlet {
    private static Map<String, Product> productMap = new HashMap<>();

    static class Product {
        String name;
        String price;
        String description;
        String specs;

        Product(String name, String price, String description, String specs) {
            this.name = name;
            this.price = price;
            this.description = description;
            this.specs = specs;
        }

        @Override
        public String toString() {
            return "Tên: " + name + "\nGiá: " + price + " VND\nMô tả: " + description + "\nThông số: " + specs;
        }
    }

    static {
        productMap.put("phone", new Product("iPhone 15", "25000000", "Mobile flagship with great camera", "6.1\" Super Retina XDR, A16 Bionic"));
        productMap.put("laptop", new Product("Dell XPS 13", "35000000", "Ultra-thin laptop with premium build", "13.4\" 4K UHD, Intel i7"));
        productMap.put("accessory", new Product("AirPods Pro", "6000000", "Noise-cancelling earbuds", "Active Noise Cancellation, USB-C"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String page = request.getParameter("page");
        if (page == null || page.isEmpty()) page = "ChatBox";
        session.setAttribute("currentPage", page);

        // Kiểm tra cookie sở thích
        String preferences = getCookieValue(request, "userPreferences");
        if (preferences != null) {
            request.setAttribute("preferences", "Sở thích của bạn: " + preferences);
        }
        request.getRequestDispatcher("/" + page + ".jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String query = request.getParameter("query");

        // Phân tích cảm xúc
        String sentiment = analyzeSentiment(query);
        Product product = productMap.getOrDefault(query.toLowerCase().replaceAll("[^a-zA-Z]", ""), 
            new Product("Không tìm thấy", "", "", ""));
        
        // Xử lý sở thích
        String[] queryWords = query.toLowerCase().split("\\s+");
        String preference = "";
        for (String word : queryWords) {
            if (productMap.containsKey(word)) {
                preference = word;
                break;
            }
        }
        if (!preference.isEmpty()) {
            Cookie prefCookie = new Cookie("userPreferences", preference);
            prefCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
            response.addCookie(prefCookie);
        }

        // Xây dựng phản hồi
        String responseText = product.toString() + "\nCảm xúc của bạn: " + sentiment;
        if (!preference.isEmpty()) responseText += "\nĐã ghi nhận sở thích: " + preference;

        // Lưu lịch sử vào session
        @SuppressWarnings("unchecked")
        Map<String, String> chatHistory = (Map<String, String>) session.getAttribute("chatHistory");
        if (chatHistory == null) chatHistory = new HashMap<>();
        chatHistory.put("user: " + query, "agent: " + responseText);
        session.setAttribute("chatHistory", chatHistory);

        // Trả về phản hồi dạng text cho AJAX
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(responseText);
    }

    private String analyzeSentiment(String query) {
        query = query.toLowerCase();
        if (query.contains("tốt") || query.contains("hay") || query.contains("thích") || query.contains("đẹp")) {
            return "Tích cực (Bạn có vẻ hài lòng!)";
        } else if (query.contains("xấu") || query.contains("kém") || query.contains("ghét") || query.contains("tệ")) {
            return "Tiêu cực (Mình sẽ cố gắng hơn!)";
        } else if (query.contains("?") || query.contains("hỏi")) {
            return "Trung lập (Bạn đang hỏi gì đó, mình sẽ giúp!)";
        } else {
            return "Trung lập";
        }
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}