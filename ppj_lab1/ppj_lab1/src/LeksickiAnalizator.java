import java.util.*;

public class LeksickiAnalizator {
    public static List<String> keyWords = List.of("od", "do", "az", "za");
    public static List<Character> num = List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
    public static List<Character> operators = List.of('*', '=', '+', '-', '/', '(', ')');
    public static int rowCounter = 0;

    public static void main(String[] args) {
        // write your code here
        Scanner sc = new Scanner(System.in);
        //System.out.println("Unesite:");
        String line = "";


        while (sc.hasNextLine()) {
            rowCounter++;
            if (!(line = sc.nextLine()).isEmpty()) {
                String[] arr;
                arr = line.trim().split(" ");
                boolean kom = false;
                for (String value : arr) {
                    Character[] ch = new Character[value.length()];
                    for (int j = 0; j < value.length(); j++) {
                        ch[j] = value.charAt(j);
                    }
                    String type;
                    int start = 0;
                    int end = 0;
                    Character[] printArr;
                    while (start < ch.length) {
                        if (ch[start].equals('/')) { //eliminate comments
                            if (start + 1 < ch.length) {
                                if (ch[start + 1].equals('/')) {
                                    kom = true;
                                    break;
                                }
                            }
                        }

                        if (checkTypeFunction(ch[start]).equals("op")) {
                            printFunctionOp(ch[start], rowCounter);
                            start++;
                            end = start;
                        } else {

                            if (end < ch.length - 1 && !(ch.length == 1)) { //if it's not the last one in array
                                end = start + 1; //next one
                            }
                            boolean breaked = false;
                            while (checkTypeFunction(ch[end]).equals(checkTypeFunction(ch[start]))) { //ispitivanje duljjne
                                if (end == ch.length - 1) {
                                    breaked = true;
                                    break;
                                } else {
                                    end++;

                                }
                            }
                            if (!breaked) {
                                end--;
                            }
                            printArr = new Character[end - start + 1];
                            type = checkTypeFunction(ch[start]);
                            int border = end - start + 1;
                            for (int k = 0; k < border; k++, start++) {
                                printArr[k] = ch[start];
                            }
                            printFunction(type, printArr, rowCounter);
                        }
                    }

                    if (kom) {
                        break;
                    }


                }

            }
        }

        sc.close();
    }

    private static void printFunction(String type, Character[] printArr, int rowCounter) {
        StringBuilder sb = new StringBuilder();
        StringBuilder help = new StringBuilder();
        if (type.equals("num")) {
            sb.append("BROJ");
            sb.append(" ");
            sb.append(String.valueOf(rowCounter));
            sb.append(" ");
            for (Character ch : printArr) {
                sb.append(ch);
            }
            System.out.println(sb.toString());
        } else if (printArr.length == 2 && keyWords.contains(help.append(printArr[0]).append(printArr[1]).toString())) {
            String x = help.toString();
            sb.append("KR_");
            switch (x) {
                case "za":
                    sb.append("ZA");
                    sb.append(" ");
                    sb.append(String.valueOf(rowCounter));
                    sb.append(" ");
                    sb.append("za");
                    break;
                case "az":
                    sb.append("AZ");
                    sb.append(" ");
                    sb.append(String.valueOf(rowCounter));
                    sb.append(" ");
                    sb.append("az");
                    break;
                case "od":
                    sb.append("OD");
                    sb.append(" ");
                    sb.append(String.valueOf(rowCounter));
                    sb.append(" ");
                    sb.append("od");
                    break;
                case "do":
                    sb.append("DO");
                    sb.append(" ");
                    sb.append(String.valueOf(rowCounter));
                    sb.append(" ");
                    sb.append("do");
                    break;
                default:
            }
            System.out.println(sb);
        } else { //identifikators
            sb.append("IDN");
            sb.append(" ");
            sb.append(String.valueOf(rowCounter));
            sb.append(" ");
            for (Character c : printArr) {
                sb.append(c);
            }
            System.out.println(sb.toString());
        }
    }

    private static String checkTypeFunction(Character param) {
        if (num.contains(param)) {
            return "num";
        } else if (operators.contains(param)) {
            return "op";
        } else {
            return "word";
        }

    }

    private static void printFunctionOp(Character value, int rowCounter) {
        if (value.equals('+')) {
            System.out.println("OP_PLUS " + rowCounter + " +");
        } else if (value.equals('-')) {
            System.out.println("OP_MINUS " + rowCounter + " -");
        } else if (value.equals('=')) {
            System.out.println("OP_PRIDRUZI " + rowCounter + " =");
        } else if (value.equals('*')) {
            System.out.println("OP_PUTA " + rowCounter + " *");
        } else if (value.equals('/')) {
            System.out.println("OP_DIJELI " + rowCounter + " /");
        } else if (value.equals('(')) {
            System.out.println("L_ZAGRADA " + rowCounter + " (");
        } else {
            System.out.println("D_ZAGRADA " + rowCounter + " )");
        }
    }
}
