package kz.kbtu.file_filter;


import org.springframework.stereotype.Component;

@Component
public class DataTypeDetector {

    public boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isFloat(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        String trimmed = str.trim();

        try {
            Double.parseDouble(trimmed);
            return trimmed.contains(".") ||
                    trimmed.toLowerCase().contains("e") ||
                    trimmed.toLowerCase().contains("infinity") ||
                    trimmed.toLowerCase().contains("nan");
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
