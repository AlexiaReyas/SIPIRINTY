package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import framework.annotation.WebApi;
import framework.annotation.Authenticated;
import framework.annotation.RoleAllowed;
import framework.util.Mapping;
import framework.util.MapFile;
import framework.util.ModAndView;
import framework.util.MySession;
import framework.util.UrlMethod;

@MultipartConfig
public class FrontController extends HttpServlet {

    private static final String CONTROLLERS_ATTRIBUTE = FrontController.class.getName() + ".controllers";
    Map<UrlMethod, Mapping> urlMapping;
    String viewPrefix;
    String viewSuffix;
    String authenticationAttribute;
    String roleAttribute;
    ApplicationContext springContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        urlMapping = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("urlMapping");
        viewPrefix = (String) getServletContext().getAttribute("prefix");
        viewSuffix = (String) getServletContext().getAttribute("suffix");
        springContext = (ApplicationContext) getServletContext().getAttribute("springContext");
        authenticationAttribute = getInitParameter("auth.session.attribute");
        if (authenticationAttribute == null || authenticationAttribute.isBlank()) {
            authenticationAttribute = "authenticatedUser";
        }
        roleAttribute = getInitParameter("auth.session.roles.attribute");
        if (roleAttribute == null || roleAttribute.isBlank()) {
            roleAttribute = "roles";
        }

        if (urlMapping == null) {
            throw new ServletException("urlMapping not initialized.");
        }
        if (getServletContext().getAttribute(CONTROLLERS_ATTRIBUTE) == null) {
            getServletContext().setAttribute(CONTROLLERS_ATTRIBUTE,
                    new ConcurrentHashMap<Class<?>, Object>());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String path = uri.substring(context.length());
        String httpMethod = req.getMethod();

        UrlMethod key = new UrlMethod(path, httpMethod);
        Mapping mapping = urlMapping.get(key);

        if (mapping != null) {
            try {
                Method controllerMethod = mapping.getMethod();
                if (controllerMethod.isAnnotationPresent(Authenticated.class)
                        && !isAuthenticated(controllerMethod, req)) {
                    rejectUnauthenticated(resp);
                    return;
                }
                if (controllerMethod.isAnnotationPresent(RoleAllowed.class)) {
                    if (!isAuthenticated(req, authenticationAttribute)) {
                        rejectUnauthenticated(resp);
                        return;
                    }
                    if (!hasAllowedRole(controllerMethod, req)) {
                        rejectForbidden(resp);
                        return;
                    }
                }

                Object controller = getController(mapping.getControllerClass());
                Object[] parameters = resolveParameters(controllerMethod, req, resp);

                Object result;
                try {
                    result = controllerMethod.invoke(controller, parameters);
                } finally {
                    synchronizeSessions(req, parameters);
                }

                if (controllerMethod.isAnnotationPresent(WebApi.class)) {
                    writeJsonResponse(resp, result);
                    return;
                }

                if (result instanceof ModAndView mav) {
                    for (Map.Entry<String, Object> en : mav.getValues().entrySet()) {
                        req.setAttribute(en.getKey(), en.getValue());
                    }

                    if (mav.getView() != null && !mav.getView().isBlank()) {
                        String viewPath = viewPrefix + mav.getView() + viewSuffix;
                        RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);
                        dispatcher.forward(req, resp);
                        return;
                    }

                    throw new ServletException("No view defined for " + key);
                }

                if (result instanceof String text) {
                    resp.setContentType("text/plain;charset=UTF-8");
                    try (PrintWriter out = resp.getWriter()) {
                        out.println("Method result:");
                        out.println(text);
                    }
                    return;
                }

                throw new ServletException("Unsupported return type for " + key + " : "
                        + (result != null ? result.getClass().getName() : "null"));

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to execute method for " + key, e);
            }
        } else {
            resp.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.println("No mapping found for URL: " + path);
                out.println("Available URLs:");

                for (UrlMethod k : urlMapping.keySet()) {
                    out.println(" - " + k);
                }
            }
        }
    }

    private boolean isAuthenticated(Method controllerMethod, HttpServletRequest req) {
        Authenticated annotation = controllerMethod.getAnnotation(Authenticated.class);
        String attributeName = annotation.value().isBlank()
                ? authenticationAttribute : annotation.value();
        return isAuthenticated(req, attributeName);
        }

        private boolean isAuthenticated(HttpServletRequest req, String attributeName) {
        return req.getSession(false) != null
                && req.getSession(false).getAttribute(MySession.STORAGE_KEY) instanceof Map<?, ?> attributes
                && attributes.containsKey(attributeName);
    }

    private void rejectUnauthenticated(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication required to access this action");
    }

    private void rejectForbidden(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                "The authenticated user does not have the required role");
    }

    private boolean hasAllowedRole(Method controllerMethod, HttpServletRequest req) {
        RoleAllowed annotation = controllerMethod.getAnnotation(RoleAllowed.class);
        Set<String> userRoles = new HashSet<>();
        HttpSession session = req.getSession(false);
        Object stored = ((Map<?, ?>) session.getAttribute(MySession.STORAGE_KEY)).get(roleAttribute);
        collectRoles(stored, userRoles);

        for (String requiredRole : annotation.value()) {
            if (userRoles.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }

    private void collectRoles(Object value, Set<String> roles) {
        if (value instanceof Collection<?> collection) {
            for (Object role : collection) {
                collectRoles(role, roles);
            }
            return;
        }
        if (value != null && value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                collectRoles(Array.get(value, i), roles);
            }
            return;
        }
        if (value instanceof String text) {
            for (String role : text.split(",")) {
                if (!role.isBlank()) {
                    roles.add(role.trim());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object getController(Class<?> controllerClass) throws ServletException {
        Map<Class<?>, Object> controllers = (Map<Class<?>, Object>) getServletContext()
                .getAttribute(CONTROLLERS_ATTRIBUTE);
        if (controllers == null) {
            throw new ServletException("Controller cache not initialized");
        }

        Object controller = controllers.get(controllerClass);
        if (controller != null) {
            return controller;
        }

        synchronized (controllers) {
            controller = controllers.get(controllerClass);
            if (controller == null) {
                try {
                    controller = controllerClass.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new ServletException("Unable to create controller '"
                            + controllerClass.getName() + "'", e);
                }
                controllers.put(controllerClass, controller);
            }
        }
        return controller;
    }

    private void writeJsonResponse(HttpServletResponse resp, Object result)
            throws IOException, ServletException {
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.write(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new ServletException("Unable to serialize API response as JSON", e);
        }
    }

    private Object[] resolveParameters(Method controllerMethod,
            HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Parameter[] methodParameters = controllerMethod.getParameters();
        Object[] values = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            Parameter parameter = methodParameters[i];
            Class<?> parameterType = parameter.getType();

            if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                values[i] = springContext;
                continue;
            }
            if (HttpServletRequest.class.isAssignableFrom(parameterType)) {
                values[i] = req;
                continue;
            }
            if (HttpServletResponse.class.isAssignableFrom(parameterType)) {
                values[i] = resp;
                continue;
            }
            if (parameterType == MySession.class) {
                values[i] = loadSession(req);
                continue;
            }
            if (isFileType(parameterType)) {
                values[i] = bindFile(req, parameter.getName(), parameterType);
                continue;
            }

            if (isSimpleParameterType(parameterType)) {
                String rawValue = req.getParameter(parameter.getName());
                values[i] = convertParameter(rawValue, parameterType, parameter.getName());
            } else {
                values[i] = bindObject(parameterType, req);
            }
        }

        return values;
    }

    private MySession loadSession(HttpServletRequest req) {
        HttpSession httpSession = req.getSession(true);
        Object stored = httpSession.getAttribute(MySession.STORAGE_KEY);
        if (stored instanceof Map<?, ?> attributes) {
            return MySession.from(attributes);
        }
        return new MySession();
    }

    private void synchronizeSessions(HttpServletRequest req, Object[] parameters) {
        for (Object parameter : parameters) {
            if (parameter instanceof MySession session) {
                req.getSession(true).setAttribute(MySession.STORAGE_KEY, session.snapshot());
            }
        }
    }

    private boolean isSimpleParameterType(Class<?> parameterType) {
        return parameterType == String.class
                || parameterType == int.class || parameterType == Integer.class
                || parameterType == long.class || parameterType == Long.class
                || parameterType == double.class || parameterType == Double.class
                || parameterType == float.class || parameterType == Float.class
                || parameterType == short.class || parameterType == Short.class
                || parameterType == byte.class || parameterType == Byte.class
                || parameterType == boolean.class || parameterType == Boolean.class
                || parameterType == char.class || parameterType == Character.class
                || parameterType.isEnum();
    }

    private Object bindObject(Class<?> objectType, HttpServletRequest req)
            throws ServletException {
        final Object object;
        try {
            object = objectType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Object parameter type '" + objectType.getName()
                    + "' must have a no-argument constructor", e);
        }

        try {
            for (PropertyDescriptor property : Introspector
                    .getBeanInfo(objectType, Object.class).getPropertyDescriptors()) {
                Method setter = property.getWriteMethod();
                if (setter == null) {
                    continue;
                }

                Class<?> propertyType = property.getPropertyType();
                if (isFileType(propertyType)) {
                    Object file = bindFile(req, property.getName(), propertyType);
                    if (file != null) {
                        setter.invoke(object, file);
                    }
                    continue;
                }

                String rawValue = req.getParameter(property.getName());
                if (rawValue == null) {
                    continue;
                }

                if (!isSimpleParameterType(propertyType)) {
                    throw new ServletException("Unsupported nested object property '"
                            + property.getName() + "' on type " + objectType.getName());
                }

                Object value = convertParameter(rawValue, propertyType, property.getName());
                setter.invoke(object, value);
            }
        } catch (java.beans.IntrospectionException | IllegalAccessException
                | InvocationTargetException e) {
            throw new ServletException("Unable to bind request parameters to object type '"
                    + objectType.getName() + "'", e);
        }

        return object;
    }

    private boolean isFileType(Class<?> parameterType) {
        return parameterType == Part.class || parameterType == MapFile.class
                || parameterType == byte[].class;
    }

    private Object bindFile(HttpServletRequest req, String parameterName, Class<?> parameterType)
            throws ServletException {
        if (!isMultipart(req)) {
            return null;
        }

        try {
            Part part = req.getPart(parameterName);
            if (part == null || part.getSubmittedFileName() == null
                    || part.getSubmittedFileName().isBlank()) {
                return null;
            }
            if (parameterType == Part.class) {
                return part;
            }
            byte[] content;
            try (InputStream input = part.getInputStream()) {
                content = input.readAllBytes();
            }
            if (parameterType == byte[].class) {
                return content;
            }
            return new MapFile(part.getSubmittedFileName(), part.getContentType(), content);
        } catch (IOException e) {
            throw new ServletException("Unable to read uploaded file '" + parameterName + "'", e);
        }
    }

    private boolean isMultipart(HttpServletRequest req) {
        String contentType = req.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    private Object convertParameter(String rawValue, Class<?> parameterType, String parameterName)
            throws ServletException {
        if (rawValue == null || rawValue.isBlank()) {
            if (parameterType.isPrimitive()) {
                throw new ServletException("Missing value for primitive parameter '"
                        + parameterName + "'");
            }
            return null;
        }

        try {
            if (parameterType == String.class) return rawValue;
            if (parameterType == int.class || parameterType == Integer.class) return Integer.valueOf(rawValue);
            if (parameterType == long.class || parameterType == Long.class) return Long.valueOf(rawValue);
            if (parameterType == double.class || parameterType == Double.class) return Double.valueOf(rawValue);
            if (parameterType == float.class || parameterType == Float.class) return Float.valueOf(rawValue);
            if (parameterType == short.class || parameterType == Short.class) return Short.valueOf(rawValue);
            if (parameterType == byte.class || parameterType == Byte.class) return Byte.valueOf(rawValue);
            if (parameterType == boolean.class || parameterType == Boolean.class) return Boolean.valueOf(rawValue);
            if (parameterType == char.class || parameterType == Character.class) return rawValue.charAt(0);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new ServletException("Invalid value '" + rawValue + "' for parameter '"
                    + parameterName + "' of type " + parameterType.getSimpleName(), e);
        }

        throw new ServletException("Unsupported controller parameter '" + parameterName
                + "' of type " + parameterType.getName());
    }
}