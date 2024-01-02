package businessImp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExceptionMethod {

    public static int validateInteger(Scanner scanner) {
        do {
            try {
                int number = Integer.parseInt(scanner.nextLine().trim());
                if (number >= 0) {
                    return number;
                } else {
                    System.err.println("Giá trị số nguyên >= 0 vui lòng nhập lại");
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Vui lòng nhập số nguyên");
            } catch (java.lang.Exception ex) {
                System.err.println(ex.getMessage());
            }
        } while (true);
    }
    //Bắt lỗi số thuc
    public static float validateFloat(Scanner scanner) {
        do {
            try {
                float number = Float.parseFloat(scanner.nextLine().trim());
                if (number > 0) {
                    return number;
                } else {
                    System.err.println("Giá trị số thực > 0 vui lòng nhập lại");
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Vui lòng nhập số thực");
            } catch (java.lang.Exception ex) {
                System.err.println(ex.getMessage());
            }
        } while (true);
    }

    //Bắt lỗi chuỗi không để trống
    public static String StringNotEmpty(Scanner scanner) {
        do {
            try {
                String s = scanner.nextLine().trim();
                if (s.trim().isEmpty()) {
                    System.err.println("Giá trị không được để trống.");
                }else {
                    return s;
                }
            } catch (java.lang.Exception ex) {
                System.err.println(ex.getMessage());
            }
        } while (true);
    }

    public static short validateShort(Scanner scanner){
        do {
            try {
                short number = Short.parseShort(scanner.nextLine().trim());
                if (number >= 0) {
                    return number;
                } else {
                    System.err.println("Giá trị số nguyên >= 0 vui lòng nhập lại");
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Vui lòng nhập số nguyên");
            } catch (java.lang.Exception ex) {
                System.err.println(ex.getMessage());
            }
        } while (true);
    }

    public static Date validateDate(Scanner scanner){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        do {
            try {
                return sdf.parse(scanner.nextLine());
            } catch (ParseException e) {
                System.err.println("Định dạng ngày dd/MM/yyyy, vui lòng nhập lại");
            }
        } while (true);
    }
}
