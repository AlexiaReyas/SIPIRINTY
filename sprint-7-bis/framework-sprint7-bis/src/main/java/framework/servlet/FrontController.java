package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import framework.annotation.WebApi;
import framework.util.Mapping;
import framework.util.ModAndView;
import framework.util.UrlMethod;

public class FrontController extends HttpServlet {

    Map<UrlMethod, Mapping> urlMapping;
    String viewPrefix;
    String viewSuffix;
    ApplicationContext springContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        urlMapping = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("urlMapping");
        viewPrefix = (String) getServletContext().getAttribute("prefix");
        viewSuffix = (String) getServletContext().getAttribute("suffix");
        springContext = (ApplicationContext) getServletContext().getAttribute("springContext");

        if (urlMapping == null) {
            throw new ServletException("urlMapping not initialized.");
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
                Object controller = mapping.getControllerClass().getDeclaredConstructor().newInstance();
                Method controllerMethod = mapping.getMethod();
                Object[] parameters = resolveParameters(controllerMethod, req, resp);

                Object result = controllerMethod.invoke(controller, parameters);

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

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
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

            if (isSimpleParameterType(parameterType)) {
                String rawValue = req.getParameter(parameter.getName());
                values[i] = convertParameter(rawValue, parameterType, parameter.getName());
            } else {
                values[i] = bindObject(parameterType, req);
            }
        }

        return values;
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

                String rawValue = req.getParameter(property.getName());
                if (rawValue == null) {
                    continue;
                }

                Class<?> propertyType = property.getPropertyType();
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