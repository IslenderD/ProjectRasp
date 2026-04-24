//package resources.Security;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.regex.Pattern;
//
//@WebFilter("/api/*")
//public class Firewall implements Filter {
//
//    /// Exact static paths.
//    private static final Map<String, Set<String>> protectedRoutes = new HashMap<>();
//
//    /// Dynamic paths using regex patterns.
//    private static final Map<Pattern, Set<String>> protectedPatterns = new HashMap<>();
//
//    private static final Map<Pattern, Set<String>> blockedPatterns = new HashMap<>();
//
//    /**
//     * All the different blocked routes.
//     * If it does not have in the Path {id} or {filename} include in protectedRoutes following structure.
//     * If it has {id}, add /\\d+ at the end of the normal url on protectedPatterns.
//     * If it has {filename}, add /.+ at the end of the normal url on protectedPatterns.
//     * null = every request is blocked
//     */
//    static {
//        // Static routes
//        protectedRoutes.put("/backend/auth/change-password", null);
//        protectedRoutes.put("/backend/auth/logout", null);
//        protectedRoutes.put("/backend/auth", Set.of("DELETE"));
//        protectedRoutes.put("/backend/move", Set.of("POST"));
//        protectedRoutes.put("/backend/move/joinPlayer", Set.of("POST"));
//        protectedRoutes.put("/backend/move/leavePlayer", Set.of("DELETE"));
//        protectedRoutes.put("/backend/move/createGame", Set.of("POST"));
//        protectedRoutes.put("/backend/game/turn", Set.of("POST"));
//        protectedRoutes.put("/backend/game/win", Set.of("POST"));
//        protectedRoutes.put("/backend/chat", null);
//    }
//
//    /**
//     * Filters each request. Checks if request is on the blocked list, and if user does not have admin cookie, blocks
//     * request.
//     *
//     * @param req   the incoming request
//     * @param res   response of the servlet if request is blocked or not
//     * @param chain chain of requests to pass request along if not blocked
//     * @throws IOException      Exception if response does not have writer or chain.DoFilter causes an IOException.
//     * @throws ServletException Exception if chain is null
//     */
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        String path = request.getRequestURI();
//        String method = request.getMethod();
//        if (isProtected(path, method)) { //if path/method on the blocked list, check if it has admin cookie
//            HttpSession session = request.getSession(false);
//
//            String origin = request.getHeader("Origin"); //it can be either, so i check both
//            String referer = request.getHeader("Referer");
//            String remoteAddr = request.getRemoteAddr(); // e.g. 127.0.0.1 for localhost
//
//            boolean isLocalhost = false;
//
//            // Allow localhost connections (useful for development or internal backend calls)
//            if (origin != null && origin.contains("localhost:8080")) {
//                isLocalhost = true;
//            } else if (referer != null && referer.contains("localhost:8080")) {
//                isLocalhost = true;
//            } else if ("127.0.0.1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr)) {
//                isLocalhost = true; // from same Raspberry
//            } else if (remoteAddr.startsWith("192.168.") || remoteAddr.startsWith("10.")) {
//                isLocalhost = true; // from local network
//            }
//            if (session == null || !isLocalhost) {
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                response.getWriter().write("Access to this request is blocked.");
//                response.flushBuffer();
//            }
//            else {
//                chain.doFilter(req, res);
//            }
//        }
//        else {
//            chain.doFilter(req, res); //makes code continue, request is accepted
//        }
//    }
//
//    /**
//     * Checks if request is one of the protected ones.
//     *
//     * @param path   url being searched
//     * @param method the HTTP method (GET, POST, etc.)
//     * @return false=request not protected, anyone can access; false=request blocked, only admin.
//     */
//    private boolean isProtected(String path, String method) {
//        if (protectedRoutes.containsKey(path)) {
//            Set<String> methods = protectedRoutes.get(path);
//            return methods == null || methods.contains(method);
//        }
//
//        for (Map.Entry<Pattern, Set<String>> entry : protectedPatterns.entrySet()) {
//            if (entry.getKey().matcher(path).matches()) {
//                Set<String> methods = entry.getValue();
//                return methods == null || methods.contains(method);
//            }
//        }
//        return false;
//    }
//
//}
