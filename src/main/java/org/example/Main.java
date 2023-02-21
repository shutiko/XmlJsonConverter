package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.w3c.dom.ls.LSOutput;
import org.xml.sax.SAXException;

import javax.crypto.spec.PSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.xml";
        String jsonFileName = "data.json";
        List<Employee> list1 = parseXML(fileName);
        String json = listToJson(list1);
        writeString(jsonFileName, json);
    }

    public static List<Employee> parseXML(String filename) {
        List<Employee> list1;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            doc = builder.parse(new File(filename));
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();
        //System.out.println("Корневой элемент: " + root.getNodeName());
        list1 = read(root);
        return list1;
    }

    private static List<Employee> read(Node node) {
        List<Employee> employeeList = new ArrayList<>();
        Employee employee;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node employeeNode = nodeList.item(i);
            // Если нода не текст, то это сотрудник - заходим внутрь
            if (employeeNode.getNodeType() != Node.TEXT_NODE) {
                NodeList employeeProps = employeeNode.getChildNodes();
                employee = new Employee();
                for (int j = 0; j < employeeProps.getLength(); j++) {
                    Node employeeProp = employeeProps.item(j);
                    // Если нода не текст, то это один из параметров сотрудника
                    if (employeeProp.getNodeType() != Node.TEXT_NODE) {
                        //System.out.println(employeeProp.getNodeName() + ":" + employeeProp.getChildNodes().item(0).getTextContent());
                        if (employeeProp.getNodeName().equals("id")) {
                            employee.id = Long.parseLong(employeeProp.getChildNodes().item(0).getTextContent());
                        } else if (employeeProp.getNodeName().equals("firstName")) {
                            employee.firstName = employeeProp.getChildNodes().item(0).getTextContent();
                        } else if (employeeProp.getNodeName().equals("lastName")) {
                            employee.lastName = employeeProp.getChildNodes().item(0).getTextContent();
                        } else if (employeeProp.getNodeName().equals("country")) {
                            employee.country = employeeProp.getChildNodes().item(0).getTextContent();
                        } else if (employeeProp.getNodeName().equals("age")) {
                            employee.age = Integer.parseInt(employeeProp.getChildNodes().item(0).getTextContent());
                        }
                    }
                }
                employeeList.add(employee);
                //System.out.println("===========>>>>");
            }
        }
        return employeeList;
    }

    public static <T> String listToJson(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String filename, String jsonString) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        ) {
            JsonObject jsonObject = new JsonObject();
            bufferedWriter.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}