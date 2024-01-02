package Presentation.Admin;

import businessImp.AccountBusiness;
import businessImp.ExceptionMethod;
import entity.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountPresentation {

    public static AccountBusiness accountBusiness = new AccountBusiness();

    public static List<Account> accountList = new ArrayList<>();
    public static void menuAccount(Scanner scanner) {
        do {
            System.out.println("******************ACCOUNT MANAGEMENT****************\n" +
                    "1. Danh sách tài khoản\n" +
                    "2. Tạo tài khoản mới\n" +
                    "3. Cập nhật thông tin tài khoản\n" +
                    "4. Tìm kiếm tài khoản\n" +
                    "5. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice) {
                case 1:
                    accountBusiness.displayAccount(accountList);
                    break;
                case 2:
                    accountBusiness.inputAccount(scanner);
                    break;
                case 3:
                    accountBusiness.updateInforAccount(scanner);
                    break;
                case 4:
                    accountBusiness.searchAccount(scanner,accountList);
                    break;
                case 5:
                    AdminPresentation.menuAdmin(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 5!");
                    break;
            }
        } while (true);
    }
}
