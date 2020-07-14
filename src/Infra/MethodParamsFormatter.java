package Infra;

import java.util.ArrayList;
import java.util.List;

public class MethodParamsFormatter {

    public static String format(String methodParams) {
        List<String> paramList = new ArrayList<>();
        if (methodParams.equals("()"))
            return methodParams;
        else {
            methodParams = methodParams.substring(1, methodParams.length() - 1);
            String individualMethodParam[] = methodParams.split(";");
            for (String param : individualMethodParam) {
                String arraySymbolTemp = "";
                for (int i = 0; i < param.length(); i++) {
                    String tokenResult = formatTokens(param.charAt(i));
                    if(tokenResult == null)
                        continue;
                    else if (tokenResult.equals("reference")) {
                        String paramPackage[] = param.split("/");
                        String paramClass = paramPackage[paramPackage.length - 1];
                        paramList.add(paramClass + arraySymbolTemp);
                        break;
                    } else if (tokenResult.equals("[]")) {
                        arraySymbolTemp = arraySymbolTemp + tokenResult;
                    } else {
                        paramList.add(tokenResult + arraySymbolTemp);
                        arraySymbolTemp = "";
                    }
                }
            }

        }
        String finalParamList = paramList.toString();
        return "("+ finalParamList.substring(1,finalParamList.length() - 1) + ")";
    }

    private static String formatTokens(char ch) {
        switch (ch) {
            case '[':
                return "[]";
            case 'L':
                return "reference";
            case 'B':
                return "byte";
            case 'C':
                return "char";
            case 'S':
                return "short";
            case 'I':
                return "int";
            case 'F':
                return "float";
            case 'J':
                return "long";
            case 'D':
                return "double";
            case 'Z':
                return "boolean";
            case 'V':
                return "void";
            default:
                return null;
        }
    }
}
