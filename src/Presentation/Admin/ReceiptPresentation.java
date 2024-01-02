package Presentation.Admin;

import businessImp.ExceptionMethod;
import businessImp.ReceiptBusiness;
import entity.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReceiptPresentation {

    public static ReceiptBusiness receiptBusiness = new ReceiptBusiness();

    public static List<Bill> billList = new ArrayList<>();

    public static void menuReceipt(Scanner scanner){
        do {
            System.out.println("******************RECEIPT MANAGEMENT****************\n" +
                    "1. Danh sách phiếu nhập\n" +
                    "2. Tạo phiếu nhập\n" +
                    "3. Cập nhật thông tin phiếu nhập\n" +
                    "4. Chi tiết phiếu nhập\n" +
                    "5. Duyệt phiếu nhập\n" +
                    "6. Tìm kiếm phiếu nhập\n" +
                    "7. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    receiptBusiness.displayReceipt(billList);
                    break;
                case 2:
                    receiptBusiness.inputReceipt(scanner);
                    break;
                case 3:
                    receiptBusiness.updateReceipt(scanner);
                    break;
                case 4:
                    receiptBusiness.detailReceipt(scanner,billList);
                    break;
                case 5:
                    receiptBusiness.confirmReceipt(scanner,billList);
                    break;
                case 6:
                    receiptBusiness.searchReceipt(scanner,billList);
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
