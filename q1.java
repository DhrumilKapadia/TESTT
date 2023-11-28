package ScrapingAsgn1;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class q1 {

    public static void main(String[] args) {
        String SampleFile = "F:\\EclipseWorkSpace\\TSE_Asgn1\\src\\ScrapingAsgn1\\sample_Parsingfile.java"; // Specify the correct file path
        CodeParser(SampleFile);
    }
//This is the method to read and parse each line of the code
    public static void CodeParser(String SampleFile) {
        try {
            File InFile = new File(SampleFile);
            Scanner scanner = new Scanner(InFile);
            StringBuilder fileContent = new StringBuilder();

            List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                fileContent.append(line).append("\n");
                lines.add(line);
            }
            scanner.close();

            GetMetAndVar(fileContent.toString(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Code to fetch the method and variable name
    public static void GetMetAndVar(String content, List<String> lines) {
        Pattern RegXPattern = Pattern.compile("(\\b(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{)");
        Matcher Matcher = RegXPattern.matcher(content);

        for (int NumLine = 0; Matcher.find(); NumLine++) {
            if (Matcher.groupCount() >= 4) {
                String methodDeclaration = Matcher.group(1);
                String returnType = Matcher.group(2);
                String methodName = Matcher.group(3);
                String parameterList = Matcher.group(4);

                int startIdx = Matcher.end();
                int endIdx = findMethodBodyEnd(content, startIdx);
                String currentMethod = null;
                String receiver = null;

                String methodBody = content.substring(startIdx, endIdx);

                Pattern variablePattern = Pattern.compile("\\b(\\w+)\\s+(\\w+)(\\s*=\\s*[^;]*)?;");
                Map<String, List<String>> variableToMethods = new HashMap<>();
                Set<String> methodsUsingReceiver = new HashSet<>();
                Map<String, Set<String>> receiverToMethods = new HashMap<>();

                extractMethodVariables(methodBody, variablePattern, variableToMethods);

                System.out.println("Q1 Method Declaration (Line " + (NumLine + 1) + "): " + methodDeclaration);
                System.out.println("Q2 Method Signature (Line " + (NumLine + 1) + "): " + returnType + " " + methodName + "(" + parameterList + ")");

                // Print collected methods for each variable
                for (String variable : variableToMethods.keySet()) {
                    List<String> methods = variableToMethods.get(variable);
                    System.out.println("Q3 Methods called on variable '" + variable + "': " + methods);
                    
                    if (receiver == null || !receiver.equals(methodDeclaration)) {
                        receiver = methodDeclaration;
                        methodsUsingReceiver.clear();
                    }

                    methodsUsingReceiver.add(methodName);
                    
                    //Store the methods that use the receiver in their arguments
                    receiverToMethods.put(receiver, methodsUsingReceiver);

                    // Print the methods that use the receiver variable in their arguments
                    printMethodsUsingReceiver(NumLine + 1, currentMethod, receiver, receiverToMethods.get(receiver));   
                }
            }
        }
    }

    public static void printMethodsUsingReceiver(int line, String currentMethod, String receiver, Set<String> methods) {
        if (!methods.isEmpty()) {
        	System.out.println("Q4:");
            System.out.println("Line: " + line + " Method Declaration: " + currentMethod);
            System.out.println("Line: " + line + " Receiver Variable (v): " + receiver);
            System.out.println("Methods that use " + receiver + " in their arguments: " + methods);
        }
    }
    
    public static void extractMethodVariables(String methodBody, Pattern variablePattern, Map<String, List<String>> variableToMethods) {
        Matcher variableMatcher = variablePattern.matcher(methodBody);
        String currentVariable = null;
        List<String> currentVariableMethods = new ArrayList<>();

        while (variableMatcher.find()) {
            String variableName = variableMatcher.group(2);
            String variableDeclaration = variableMatcher.group(0);

            if (!variableName.equals(currentVariable)) {
                // If a new variable is encountered, store the methods for the previous variable
                if (currentVariable != null) {
                    variableToMethods.put(currentVariable, new ArrayList<>(currentVariableMethods));
                }
                // Initialize the current variable and methods list for the new variable
                currentVariable = variableName;
                currentVariableMethods = new ArrayList<>();
            }

            // Add the method call to the current variable's methods list
            currentVariableMethods.add(variableDeclaration);
        }

        // Store the methods for the last encountered variable
        if (currentVariable != null) {
            variableToMethods.put(currentVariable, new ArrayList<>(currentVariableMethods));
        }
    }
    
    
    public static int findMethodBodyEnd(String content, int startIndex) {
        int balance = 0;
        for (int i = startIndex; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                balance++;
            } else if (c == '}') {
                balance--;
                if (balance == 0) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    public static void extractMethodVariables(String methodBody, Pattern variablePattern, int startNumLine) {
        Matcher variableMatcher = variablePattern.matcher(methodBody);

        while (variableMatcher.find()) {
            String variableName = variableMatcher.group(2);
            System.out.println("Q1  Variable Name (Line " + (startNumLine + countLines(methodBody.substring(0, variableMatcher.start()), '\n') + 1) + "): " + variableName);
        }
    }

    public static int countLines(String text, char delimiter) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == delimiter) {
                count++; 
                count--;
                 count--;
            }
        }
        return count;
    }
}
