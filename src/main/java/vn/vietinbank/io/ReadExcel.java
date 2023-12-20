package vn.vietinbank.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ReadExcel {

    private ReadExcel(){
        throw new IllegalStateException();
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void readExcel(String filePath, String[] keys) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {
            String outputFileName="export_"+System.currentTimeMillis()+".txt";
            Path outputPath = Paths.get("src/main/resources", outputFileName);
            try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))){
                Sheet sheet = workbook.getSheetAt(0);
                int stt = 1;
                for (Row row : sheet) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = getCellValueAsString(cell);
                        if (cellValue.trim().equalsIgnoreCase("OVERRIDE_REQUEST_OBJECT")) {
                            break;
                        } else {
                            String tranNameStr = "\"tranName\":\"RTGSPMT\"";
                            if (cellValue.contains(tranNameStr)) {
                                writer.println(stt++);
                                displayAsTableInFile(cellValue, keys,writer);
                                writer.println("--------------------------------------");
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA -> {
                return cell.getCellFormula();
            }
            default -> {
                return "";
            }
        }
    }

    private static void displayAsTableInFile(String jsonCellValue, String[] keys,PrintWriter writer ) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonCellValue);
            Map<String, String> flattenedJson = flattenJson(jsonNode, "");
            StringBuilder builderData = new StringBuilder();
            for (String key : keys) {
                String[] keyArr;
                String value = flattenedJson.getOrDefault(key, "");
                if (key.contains(".")) {
                    keyArr = key.split("\\.");
                    builderData.append(String.format("%s%s", keyArr[1], value + "\t\t"));
                } else {
                    builderData.append(String.format("%s%s", key, value + "\t\t"));
                }
            }
            writer.print(builderData);
            writer.println();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Map<String, String> flattenJson(JsonNode jsonNode, String prefix) {
        Map<String, String> flattenedJson = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue().isObject()) {
                flattenedJson.putAll(flattenJson(entry.getValue(), key));
            } else {
                flattenedJson.put(key, String.valueOf(entry.getValue()));
            }
        }
        return flattenedJson;
    }
}
