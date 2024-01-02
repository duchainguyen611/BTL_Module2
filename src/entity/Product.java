package entity;

import businessImp.ExceptionMethod;
import util.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Scanner;

public class Product {
    private String productId;
    private String productName;
    private String manufacturer;
    private Date created;
    private short batch;
    private int quantity;
    private boolean productStatus;

    public Product() {
    }

    public Product(String productId, String productName, String manufacturer, Date created, short batch, int quantity, boolean productStatus) {
        this.productId = productId;
        this.productName = productName;
        this.manufacturer = manufacturer;
        this.created = created;
        this.batch = batch;
        this.quantity = quantity;
        this.productStatus = productStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(short batch) {
        this.batch = batch;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isProductStatus() {
        return productStatus;
    }

    public void setProductStatus(boolean productStatus) {
        this.productStatus = productStatus;
    }

    public void inputData(Scanner scanner) {
        this.productId = inputProductId(scanner);
        this.productName = inputProductName(scanner);
        this.manufacturer = inputManufacturer(scanner);
        this.batch = inputBatch(scanner);
    }


    public String inputProductId(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập mã sản phẩm:");
        boolean isDuplicate;
        String id_product = null;
        try {
            do {
                isDuplicate = false;
                id_product = ExceptionMethod.StringNotEmpty(scanner);
                if (id_product.length() <= 5){
                    callSt = conn.prepareCall("{call check_product_by_id(?,?)}");
                    callSt.setString(1, id_product);
                    callSt.registerOutParameter(2, Types.INTEGER);
                    callSt.execute();
                    int cnt_id_product = callSt.getInt(2);
                    if (cnt_id_product > 0) {
                        System.err.println("Mã sản phẩm bị trùng! Mời nhập lại!");
                        isDuplicate = true;
                    }
                }else {
                    System.err.println("Mã sản phẩm chỉ từ 5 ký tự! Mời nhập lại!");
                    isDuplicate = true;
                }
            } while (isDuplicate);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return id_product;
    }

    public String inputProductName(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập tên sản phẩm:");
        boolean isDuplicate;
        String name_product = null;
        try {
            do {
                isDuplicate = false;
                name_product = ExceptionMethod.StringNotEmpty(scanner);
                callSt = conn.prepareCall("{call check_product_by_name(?,?)}");
                callSt.setString(1, name_product);
                callSt.registerOutParameter(2, Types.INTEGER);
                callSt.execute();
                int cnt_product = callSt.getInt(2);
                if (cnt_product > 0) {
                    System.err.println("Tên sản phẩm bị trùng! Mời nhập lại!");
                    isDuplicate = true;
                }
            } while (isDuplicate);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return name_product;
    }

    public String inputManufacturer(Scanner scanner) {
        System.out.println("Nhập nhà sản xuất:");
        return ExceptionMethod.StringNotEmpty(scanner);
    }

    public short inputBatch(Scanner scanner) {
        System.out.println("Nhập lô chứa sản phẩm:");
        return ExceptionMethod.validateShort(scanner);
    }

    public Date inputCreated(Scanner scanner) {
        System.out.println("Nhập ngày tạo sản phẩm:");
        return ExceptionMethod.validateDate(scanner);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", created=" + created +
                ", batch=" + batch +
                ", quantity=" + quantity +
                ", productStatus=" + productStatus +
                '}';
    }
}
