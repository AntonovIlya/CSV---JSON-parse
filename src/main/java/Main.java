import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,"data.json");
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2,"data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader parser = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(parser)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<Employee> parseXML(String s) {
        List<Employee> list = new ArrayList<>(2);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(s));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList nodeList1 = nodeList.item(i).getChildNodes();
                if (nodeList.item(i).getNodeName().equals("employee")) {
                    Employee employee = new Employee();
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        Node node = nodeList1.item(j);
                        if (node.ELEMENT_NODE == node.getNodeType()) {
                            Element element = (Element) node;
                            String txt = element.getTextContent();
                            switch (element.getNodeName()) {
                                case ("id") -> employee.setId(Long.parseLong(txt));
                                case ("firstName") -> employee.setFirstName(txt);
                                case ("lastName") -> employee.setLastName(txt);
                                case ("country") -> employee.setCountry(txt);
                                case ("age") -> employee.setAge(Integer.parseInt(txt));
                            }
                        }

                    }
                    list.add(employee);
                }

            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.getMessage();
        }
        return list;
    }
}
