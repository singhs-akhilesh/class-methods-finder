package Infra;

public class Method {
    private String methodClass;
    private String methodName;
    private String methodParams;
    private String methodReturnType;

    public Method(String methodClass, String methodName, String methodParamsAndReturnType) {
        this.methodClass = methodClass;
        this.methodName = methodName;
        splitParamAndReturnType(methodParamsAndReturnType);
    }

    public String getMethodClass() {
        return methodClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodParams() {
        return methodParams;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }
    private void splitParamAndReturnType(String methodParamsAndReturnType){
        if(methodParamsAndReturnType != null && methodParamsAndReturnType.contains(")")){
            methodParams = methodParamsAndReturnType.substring(0, methodParamsAndReturnType.indexOf(")") + 1);
            methodReturnType = methodParamsAndReturnType.substring(methodParamsAndReturnType.indexOf(")" )+ 1, methodParamsAndReturnType.length());
        }
    }
}
