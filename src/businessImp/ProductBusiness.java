package businessImp;

import Presentation.Admin.ProductPresentation;
import bussiness.ConsoleColors;
import entity.Product;
import util.ConnectionDB;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductBusiness implements IBusiness<Product, String> {
    @Override
    public List<Product> findAll() {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Product> productList = null;
        try {
            callSt = conn.prepareCall("{call get_all_infor_product()}");
            ResultSet rs = callSt.executeQuery();
            productList = new ArrayList<>();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setManufacturer(rs.getString("manufacturer"));
                product.setCreated(rs.getDate("created"));
                product.setBatch(rs.getShort("batch"));
                product.setQuantity(rs.getInt("quantity"));
                product.setProductStatus(rs.getBoolean("product_status"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return productList;
    }

    @Override
    public boolean create(Product product) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call add_infor_product(?,?,?,?)}");
            callSt.setString(1, product.getProductId());
            callSt.setString(2, product.getProductName());
            callSt.setString(3, product.getManufacturer());
            callSt.setInt(4, product.getBatch());
            callSt.executeUpdate();
            conn.commit();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return result;
    }

    public int checkId(String id_check) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call check_product_by_id(?,?)}");
            callSt.setString(1, id_check);
            callSt.registerOutParameter(2, Types.INTEGER);
            callSt.execute();
            int cnt_Product = callSt.getInt(2);
            ConnectionDB.closeConnection(conn);
            if (cnt_Product > 0) {
                return cnt_Product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateInfor(Product product) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_infor_product(?,?,?,?,?)}");
            callSt.setString(1, product.getProductId());
            callSt.setString(2, product.getProductName());
            callSt.setString(3, product.getManufacturer());
            java.util.Date createdDate = product.getCreated();
            java.sql.Timestamp createdTimestamp = new java.sql.Timestamp(createdDate.getTime());
            callSt.setTimestamp(4, createdTimestamp);
            callSt.setInt(5, product.getBatch());
            callSt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return true;
    }

    @Override
    public boolean updateStatus(Product product) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_status_product(?,?)}");
            callSt.setString(1, product.getProductId());
            callSt.setBoolean(2, product.isProductStatus());
            callSt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return true;
    }

    @Override
    public List<Product> search(String inputSearch) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        List<Product> productList = null;
        try {

            callSt1 = conn.prepareCall("{call check_product_by_name(?,?)}");
            callSt1.setString(1, inputSearch);
            callSt1.registerOutParameter(2, Types.INTEGER);
            callSt1.execute();
            int cnt_Product = callSt1.getInt(2);
            if (cnt_Product > 0) {
                callSt2 = conn.prepareCall("{call get_product_by_name(?)}");
                callSt2.setString(1, inputSearch);
                ResultSet rs = callSt2.executeQuery();
                productList = new ArrayList<>();
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getString("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setManufacturer(rs.getString("manufacturer"));
                    product.setCreated(rs.getDate("created"));
                    product.setBatch(rs.getShort("batch"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setProductStatus(rs.getBoolean("product_status"));
                    productList.add(product);
                }
                return productList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return null;
    }

    public void displayProduct(List<Product> productList) {
        productList = findAll();
        drawProductTable(productList);
    }

    public void inputProduct(Scanner scanner) {
        System.out.println("Nhập số sản phẩm muốn thêm:");
        int n = ExceptionMethod.validateInteger(scanner);
        for (int i = 0; i < n; i++) {
            System.out.printf("Sản phẩm thứ %d:\n", i + 1);
            Product product = new Product();
            product.inputData(scanner);
            boolean resultCreate = create(product);
            System.out.println(ConsoleColors.makeColor(resultNotification(resultCreate, "Thêm"),ConsoleColors.GREEN));
        }
    }

    public void updateInforProduct(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập mã cần cập nhật:");
        String id_update = ExceptionMethod.StringNotEmpty(scanner);
        if (id_update.length()<=5){
            try {
                if (checkId(id_update) > 0) {
                    callSt = conn.prepareCall("{call get_infor_to_update_product(?)}");
                    callSt.setString(1, id_update);
                    ResultSet rs = callSt.executeQuery();
                    Product product = new Product();
                    product.setProductId(id_update);
                    do {
                        System.out.println("MENU UPDATE PRODUCT");
                        System.out.println("1. Tên sản phẩm\n2. Nhà sản xuất\n3. Ngày tạo sản phẩm\n4. Lô chứa sản phẩm\n5. Thoát");
                        int choice = ExceptionMethod.validateInteger(scanner);
                        boolean resultUpdate;
                        switch (choice) {
                            case 1:
                                product.setProductName(product.inputProductName(scanner));
                                if (rs.next()) {
                                    product.setManufacturer(rs.getString("manufacturer"));
                                    product.setCreated(rs.getDate("created"));
                                    product.setBatch(rs.getShort("batch"));
                                }
                                resultUpdate = updateInfor(product);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                break;
                            case 2:
                                product.setManufacturer(product.inputManufacturer(scanner));
                                if (rs.next()) {
                                    product.setProductName(rs.getString("product_name"));
                                    product.setCreated(rs.getDate("created"));
                                    product.setBatch(rs.getShort("batch"));
                                }
                                resultUpdate = updateInfor(product);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                break;
                            case 3:
                                product.setCreated(product.inputCreated(scanner));
                                if (rs.next()) {
                                    product.setProductName(rs.getString("product_name"));
                                    product.setManufacturer(rs.getString("manufacturer"));
                                    product.setBatch(rs.getShort("batch"));
                                }
                                resultUpdate = updateInfor(product);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                break;
                            case 4:
                                product.setBatch(product.inputBatch(scanner));
                                if (rs.next()) {
                                    product.setProductName(rs.getString("product_name"));
                                    product.setManufacturer(rs.getString("manufacturer"));
                                    product.setCreated(rs.getDate("created"));
                                }
                                resultUpdate = updateInfor(product);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                break;
                            case 5:
                                ConnectionDB.closeConnection(conn);
                                ProductPresentation.menuProduct(scanner);
                                break;
                            default:
                                System.err.println("Mời nhập từ 1 đến 5!");
                                break;
                        }
                    } while (true);
                } else {
                    System.err.println("Không tồn tại mã sản phẩm! Ấn 3 để nhập lại!");
                    ProductPresentation.menuProduct(scanner);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else {
            System.err.println("Mã sản phẩm chỉ từ 5 ký tự! Ấn 3 để nhập lại!");
        }
    }

    public String resultNotification(boolean result, String types) {
        if (result) {
            return types + " thành công!";
        } else {
            return "Có lỗi trong quá trình thực hiện!";
        }
    }

    public void searchProduct(Scanner scanner, List<Product> productList) {
        System.out.println("Nhập tên sản phẩm tìm kiếm:");
        String name_Search = ExceptionMethod.StringNotEmpty(scanner);
        productList = search(name_Search);
        if (productList != null) {
            drawProductTable(productList);
        } else {
            System.err.println("Tên sản phẩm bạn nhập không tồn tại!");
        }
    }

    public void updateStatusProduct(Scanner scanner, List<Product> productList) {
        System.out.println("Danh sách sản phẩm:");
        displayProduct(productList);
        System.out.println("Mã sản phẩm cần cập nhật trạng thái:");
        String id_update_status = ExceptionMethod.StringNotEmpty(scanner);
        if(id_update_status.length() <=5){
            if (checkId(id_update_status) > 0) {
                boolean resultUpdate;
                Product product = new Product();
                product.setProductId(id_update_status);
                System.out.println("Bạn muốn cập nhật trạng thái sản phẩm thế nào?\n1. Hoạt động\n2. Không hoạt động\n3. Thoát");
                int choie = ExceptionMethod.validateInteger(scanner);
                switch (choie) {
                    case 1:
                        product.setProductStatus(true);
                        resultUpdate = updateStatus(product);
                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));;
                        break;
                    case 2:
                        product.setProductStatus(false);
                        resultUpdate = updateStatus(product);
                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));;
                        break;
                    case 3:
                        ProductPresentation.menuProduct(scanner);
                        break;
                    default:
                        System.err.println("Mời nhập từ 1 đến 3!");
                        break;
                }
            } else {
                System.err.println("Không tồn tại mã sản phẩm");
            }
        }else {
            System.err.println("Mã sản phẩm chỉ từ 5 ký tự! Ấn 5 đệ nhập lại!");
        }
    }

    public static void drawProductTable(List<Product> productList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("                                    Bảng thông tin sản phẩm                         ");
        for (int i = 0; i < 138; i++) {
            System.out.print("─");
        }
        System.out.println();
        System.out.printf("|%-15s|%-20s|%-20s|%-20s|%-20s|%-15s|%-20s|%n",
                "Mã sản phẩm",
                "Tên sản phẩm",
                "Nhà sản xuất",
                "Ngày sản xuất",
                "Lô chứa",
                "Số lượng",
                "Trạng thái");
        for (int i = 0; i < 138; i++) {
            System.out.print("─");
        }
        System.out.println();
        for (Product product : productList) {
            System.out.printf("|%-15s|%-20s|%-20s|%-20s|%-20s|%-15s|%-20s|%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getManufacturer(),
                    sdf.format(product.getCreated()),
                    product.getBatch(),
                    product.getQuantity(),
                    (product.isProductStatus()?"Hoạt động":"Không hoạt động"));
            for (int i = 0; i < 138; i++) {
                System.out.print("─");
            }
            System.out.println();
        }
    }

}
