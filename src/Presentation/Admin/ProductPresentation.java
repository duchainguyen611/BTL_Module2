package Presentation.Admin;

import businessImp.ExceptionMethod;
import businessImp.ProductBusiness;
import entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductPresentation {

    public static ProductBusiness productBusiness = new ProductBusiness();

    public static List<Product> productList = new ArrayList<>();
    public static void menuProduct(Scanner scanner){
        do {
            System.out.println("******************PRODUCT MANAGEMENT****************\n" +
                    "1. Danh sách sản phẩm\n" +
                    "2. Thêm mới sản phẩm\n" +
                    "3. Cập nhật sản phẩm\n" +
                    "4. Tìm kiếm sản phẩm\n" +
                    "5. Cập nhật trạng thái sản phẩm\n" +
                    "6. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice){
                case 1:
                    productBusiness.displayProduct(productList);
                    break;
                case 2:
                    productBusiness.inputProduct(scanner);
                    break;
                case 3:
                    productBusiness.updateInforProduct(scanner);
                    break;
                case 4:
                    productBusiness.searchProduct(scanner,productList);
                    break;
                case 5:
                    productBusiness.updateStatusProduct(scanner,productList);
                    break;
                case 6:
                    AdminPresentation.menuAdmin(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 6!");
                    break;
            }
        }while (true);
    }
}
