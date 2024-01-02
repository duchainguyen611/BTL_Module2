package Presentation.User;

import Presentation.Login.LogInPresentation;
import businessImp.ExceptionMethod;
import businessImp.LogInBusiness;
import businessImp.UserBusiness;
import entity.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserPresentation {

    public static UserBusiness userBusiness = new UserBusiness();

    public static List<Bill> billList =  new ArrayList<>();
    public static void menuUser(Scanner scanner){
        do {
            System.out.println("******************WAREHOUSE MANAGEMENT****************\n" +
                    "1. Danh sách phiếu nhập theo trạng thái\n" +
                    "2. Tạo phiếu nhập\n" +
                    "3. Cập nhật phiếu nhập\n" +
                    "4. Tìm kiếm phiếu nhập\n" +
                    "5. Xem chi tiết phiếu nhập\n" +
                    "6. Danh sách phiếu xuất theo trạng thái\n" +
                    "7. Tạo phiếu xuất\n" +
                    "8. Cập nhật phiếu xuất\n" +
                    "9. Tìm kiếm phiếu xuất\n" +
                    "10. Xem chi tiết phiếu xuất\n" +
                    "11. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    userBusiness.displayReceiptByIdEmp(billList);
                    break;
                case 2:
                    userBusiness.createReceiptByIdEmp(scanner);
                    break;
                case 3:
                    userBusiness.updateReceiptByIdEmp(scanner);
                    break;
                case 4:
                    userBusiness.searchReceiptByIdEmp(billList,scanner);
                    break;
                case 5:
                    userBusiness.detailReceipt(scanner,billList);
                    break;
                case 6:
                    userBusiness.displayBillByIdEmp(billList);
                    break;
                case 7:
                    userBusiness.createBillByIdEmp(scanner);
                    break;
                case 8:
                    userBusiness.updateBillByIdEmp(scanner);
                    break;
                case 9:
                    userBusiness.searchBillByIdEmp(billList,scanner);
                    break;
                case 10:
                    userBusiness.detailBill(scanner,billList);
                    break;
                case 11:
                    LogInBusiness.writeDataToFile(null);
                    LogInPresentation.logInMenu(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 11!");
                    break;
            }
        }while (true);
    }
}
