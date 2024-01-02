package Presentation.Admin;

import businessImp.ExceptionMethod;
import businessImp.ReportBusiness;

import java.util.Scanner;

public class ReportPresentation {

    public  static  ReportBusiness reportBusiness = new ReportBusiness();
    public static void menuReport(Scanner scanner){
        do {
            System.out.println("******************REPORT MANAGEMENT****************\n" +
                    "1. Thống kê chi phí theo ngày, tháng, năm\n" +
                    "2. Thống kê chi phí theo khoảng thời gian\n" +
                    "3. Thống kê doanh thu theo ngày, tháng, năm\n" +
                    "4. Thống kê doanh thu theo khoảng thời gian\n" +
                    "5. Thống kê số nhân viên theo từng trạng thái\n" +
                    "6. Thống kê sản phẩm nhập nhiều nhất trong khoảng thời gian\n" +
                    "7. Thống kê sản phẩm nhập ít nhất trong khoảng thời gian\n" +
                    "8. Thống kê sản phẩm xuất nhiều nhất trong khoảng thời gian\n" +
                    "9. Thống kê sản phẩm xuất ít nhất trong khoảng thời gian\n" +
                    "10.Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    reportBusiness.statisticReceiptByDay(scanner);
                    break;
                case 2:
                    reportBusiness.statisticReceiptFromDayToDay(scanner);
                    break;
                case 3:
                    reportBusiness.statisticBillByDay(scanner);
                    break;
                case 4:
                    reportBusiness.statisticBillFromDayToDay(scanner);
                    break;
                case 5:
                    reportBusiness.statisticEmpById(scanner);
                    break;
                case 6:
                    reportBusiness.statisticMaxReceiptFromDayToDay(scanner);
                    break;
                case 7:
                    reportBusiness.statisticMinReceiptFromDayToDay(scanner);
                    break;
                case 8:
                    reportBusiness.statisticMaxBillFromDayToDay(scanner);
                    break;
                case 9:
                    reportBusiness.statisticMinBillFromDayToDay(scanner);
                    break;
                case 10:
                    AdminPresentation.menuAdmin(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 10!");
                    break;
            }
        }while (true);
    }
}
