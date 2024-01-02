package entity;

import businessImp.ExceptionMethod;
import businessImp.ProductBusiness;
import util.ConnectionDB;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Bill_Detail {
    private int billDetailId;
    private int billId;
    private String productId;
    private int quantity;
    private float Price;

    public Bill_Detail() {
    }

    public Bill_Detail(int billDetailId, int billId, String productId, int quantity, float price) {
        this.billDetailId = billDetailId;
        this.billId = billId;
        this.productId = productId;
        this.quantity = quantity;
        Price = price;
    }

    public int getBillDetailId() {
        return billDetailId;
    }

    public void setBillDetailId(int billDetailId) {
        this.billDetailId = billDetailId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    @Override
    public String toString() {
        return "Bill_Detail{" +
                "billDetailId=" + billDetailId +
                ", billId=" + billId +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", Price=" + Price +
                '}';
    }

    public String inputProductId(Scanner scanner, List<Product> productList){
        System.out.println("Danh sách sản phẩm:");
        ProductBusiness productBusiness = new ProductBusiness();
        productBusiness.displayProduct(productList);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        System.out.println("Nhập mã sản phẩm:");
        boolean isCheck;
        String id_product = null;
        try {
            do {
                isCheck = false;
                id_product = ExceptionMethod.StringNotEmpty(scanner);
                if (id_product.length() <= 5){
                    callSt = conn.prepareCall("{call check_product_by_id(?,?)}");
                    callSt.setString(1, id_product);
                    callSt.registerOutParameter(2, Types.INTEGER);
                    callSt.execute();
                    int cnt_id_product = callSt.getInt(2);
                    if (cnt_id_product <= 0) {
                        System.err.println("Không tồn tại mã sản phẩm! Mời nhập lại!");
                        isCheck = true;
                    }else{
                        callSt2 = conn.prepareCall("{call get_status_product(?)}");
                        callSt2.setString(1,id_product);
                        ResultSet rs = callSt2.executeQuery();
                        boolean status = true;
                        if (rs.next()){
                            status = rs.getBoolean(1);
                        }
                        if (!status){
                            System.err.println("Trạng thái sản phẩm đang không hoạt động!");
                            isCheck = true;
                        }
                    }
                }else {
                    System.err.println("Mã sản phẩm chỉ từ 5 ký tự! Mời nhập lại!");
                    isCheck = true;
                }
            } while (isCheck);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return id_product;
    }

    public int inputQuantity(Scanner scanner){
        System.out.println("Nhập số lượng:");
        return ExceptionMethod.validateInteger(scanner);
    }

    public float inputPrice(Scanner scanner){
        System.out.println("Nhập giá:");
        return ExceptionMethod.validateFloat(scanner);
    }

}
