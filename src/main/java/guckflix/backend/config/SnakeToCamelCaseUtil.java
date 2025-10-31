package guckflix.backend.config;

public class SnakeToCamelCaseUtil {

    public static String convertSnakeToCamel(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();

        boolean capitalizeNext = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(Character.toLowerCase(c));
                }
            }
        }

        return camelCase.toString();
    }
}
