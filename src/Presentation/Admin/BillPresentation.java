package Presentation.Admin;

import businessImp.BillBusiness;
import businessImp.ExceptionMethod;
import entity.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillPresentation {

    public static BillBusiness billBusiness = new BillBusiness();

    public static List<Bill> billList = new ArrayList<>();
    public static void menuBill(Scanner scanner){
        do {
            System.out.println("******************BILL MANAGEMENT****************\n" +
                    "1. Danh sách phiếu xuất\n" +
                    "2. Tạo phiếu xuất\n" +
                    "3. Cập nhật thông tin phiếu xuất\n" +
                    "4. Chi tiết phiếu xuất\n" +
                    "5. Duyệt phiếu xuất\n" +
                    "6. Tìm kiếm phiếu xuất\n" +
                    "7. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    billBusiness.displayBill(billList);
                    break;
                case 2:
                    billBusiness.inputBill(scanner);
                    break;
                case 3:
                    billBusiness.updateBill(scanner);
                    break;
                case 4:
                    billBusiness.detailBill(scanner,billList);
                    break;
                case 5:
                    billBusiness.confirmBill(scanner,billList);
                    break;
                case 6:
                    billBusiness.searchBill(scanner,billList);
                    break;
                case 7:
                    AdminPresentation.menuAdmin(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 7!");
                    break;
            }
        }while (true);
    }
}
