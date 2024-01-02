package Presentation.Admin;

import Presentation.Login.LogInPresentation;
import businessImp.ExceptionMethod;
import businessImp.LogInBusiness;

import java.util.Scanner;

public class AdminPresentation {
    public static void menuAdmin(Scanner scanner){
        do {
            System.out.println("******************WAREHOUSE MANAGEMENT****************\n" +
                    "1. Quản lý sản phẩm\n" +
                    "2. Quản lý nhân viên\n" +
                    "3. Quản lý tài khoản\n" +
                    "4. Quản lý phiếu nhập\n" +
                    "5. Quản lý phiếu xuất\n" +
                    "6. Quản lý báo cáo\n" +
                    "7. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    ProductPresentation.menuProduct(scanner);
                    break;
                case 2:
                    EmployeePresentation.menuEmployee(scanner);
                    break;
                case 3:
                    AccountPresentation.menuAccount(scanner);
                    break;
                case 4:
                    ReceiptPresentation.menuReceipt(scanner);
                    break;
                case 5:
                    BillPresentation.menuBill(scanner);
                    break;
                case 6:
                    ReportPresentation.menuReport(scanner);
                    break;
                case 7:
                    LogInBusiness.writeDataToFile(null);
                    LogInPresentation.logInMenu(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 7!");
                    break;
            }
        }while (true);
    }
}
