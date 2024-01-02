package businessImp;

import Presentation.Admin.BillPresentation;
import Presentation.Admin.EmployeePresentation;
import Presentation.Admin.ProductPresentation;
import bussiness.ConsoleColors;
import entity.Bill;
import entity.Bill_Detail;
import util.ConnectionDB;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillBusiness implements IBusiness<Bill, String> {
    Bill_DetailBusiness billDetailBusiness = new Bill_DetailBusiness();

    @Override
    public List<Bill> findAll() {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Bill> billList = null;
        try {
            callSt = conn.prepareCall("{call get_all_infor_bill(?)}");
            callSt.setBoolean(1, false);
            ResultSet rs = callSt.executeQuery();
            billList = new ArrayList<>();
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setBillCode(rs.getString("bill_code"));
                bill.setBillType(rs.getBoolean("bill_type"));
                bill.setEmpIdCreated(rs.getString("emp_id_created"));
                bill.setCreated(rs.getDate("created"));
                bill.setEmpIdAuth(rs.getString("emp_id_auth"));
                bill.setAuthDate(rs.getDate("auth_date"));
                bill.setBillStatus(rs.getShort("bill_status"));
                billList.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return billList;
    }

    @Override
    public boolean create(Bill bill) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call add_infor_bill(?,?,?)}");
            callSt.setString(1, bill.getBillCode());
            callSt.setBoolean(2, bill.getBillType());
            callSt.setString(3, bill.getEmpIdCreated());
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

    public int checkId(String input_check) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call check_duplicate_bill_by_id_or_code(?,?,?)}");
            callSt.setString(1, input_check);
            callSt.setBoolean(2, false);
            callSt.registerOutParameter(3, Types.INTEGER);
            callSt.execute();
            int cnt_Bill = callSt.getInt(3);
            ConnectionDB.closeConnection(conn);
            if (cnt_Bill > 0) {
                return cnt_Bill;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateInfor(Bill bill) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_infor_bill(?,?,?)}");
            callSt.setInt(1, bill.getBillId());
            callSt.setString(2, bill.getEmpIdCreated());
            callSt.setShort(3, bill.getBillStatus());
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
    public boolean updateStatus(Bill bill) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call confirm_bill(?,?)}");
            callSt.setInt(1, bill.getBillId());
            callSt.setString(2, LogInBusiness.account.getEmpId());
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
    public List<Bill> search(String inputSearch) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        List<Bill> billList = null;
        try {
            callSt1 = conn.prepareCall("{call check_bill_by_id_bill_or_code_bill(?,?,?)}");
            callSt1.setString(1, inputSearch);
            callSt1.setBoolean(2, false);
            callSt1.registerOutParameter(3, Types.INTEGER);
            callSt1.execute();
            int cnt_Bill = callSt1.getInt(3);
            if (cnt_Bill > 0) {
                callSt2 = conn.prepareCall("{call get_bill_by_id_bill_or_code_bill(?,?)}");
                callSt2.setString(1, inputSearch);
                callSt2.setBoolean(2, false);
                ResultSet rs = callSt2.executeQuery();
                billList = new ArrayList<>();
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setBillCode(rs.getString("bill_code"));
                    bill.setBillType(rs.getBoolean("bill_type"));
                    bill.setEmpIdCreated(rs.getString("emp_id_created"));
                    bill.setCreated(rs.getDate("created"));
                    bill.setEmpIdAuth(rs.getString("emp_id_auth"));
                    bill.setAuthDate(rs.getDate("auth_date"));
                    bill.setBillStatus(rs.getShort("bill_status"));
                    billList.add(bill);
                }
                return billList;
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

    public void displayBill(List<Bill> billList) {
        billList = findAll();
        drawBillTable(billList);
    }

    public void inputBill(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập số phiếu xuất muốn thêm:");
        int n = ExceptionMethod.validateInteger(scanner);
        for (int i = 0; i < n; i++) {
            System.out.printf("Phiếu xuất thứ %d:\n", i + 1);
            Bill bill = new Bill();
            bill.setBillCode(bill.inputBillCode(scanner));
            bill.setBillType(false);
            bill.setEmpIdCreated(bill.inputEmpIdCreated(scanner, EmployeePresentation.employeeList));
            boolean resultCreateBill = create(bill);
            System.out.println("Nhập số sản phẩm muốn xuất:");
            int m = ExceptionMethod.validateInteger(scanner);
            for (int j = 0; j < m; j++) {
                Bill_Detail billDetail = new Bill_Detail();
                try {
                    callSt = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt.setString(1, bill.getBillCode());
                    ResultSet rs = callSt.executeQuery();
                    if (rs.next()) {
                        billDetail.setBillId(rs.getInt(1));
                    }
                    billDetail.setProductId(billDetail.inputProductId(scanner, ProductPresentation.productList));
                    billDetail.setQuantity(billDetail.inputQuantity(scanner));
                    billDetail.setPrice(billDetail.inputPrice(scanner));
                    boolean resultCreateBillDetail = billDetailBusiness.create(billDetail);
                    System.out.println(ConsoleColors.makeColor(resultNotification(resultCreateBill, resultCreateBillDetail, "Thêm"),ConsoleColors.GREEN));
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void updateBill(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        CallableStatement callSt3 = null;
        CallableStatement callSt4 = null;
        CallableStatement callSt5 = null;
        CallableStatement callSt6 = null;
        System.out.println("Nhập mã phiếu hoặc mã code cần cập nhật:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            try {
                if (checkId(input_check) > 0) {
                    callSt3 = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt3.setString(1, input_check);
                    ResultSet rs = callSt3.executeQuery();
                    int id_update = 0;
                    if (rs.next()) {
                        id_update = rs.getInt(1);
                    }
                    callSt4 = conn.prepareCall("{call get_status_bill_by_id(?)}");
                    callSt4.setInt(1, id_update);
                    ResultSet rs3 = callSt4.executeQuery();
                    short status = 0;
                    if (rs3.next()) {
                        status = rs3.getShort(1);
                    }
                    if (status != 2) {
                        callSt5 = conn.prepareCall("{call get_infor_bill_by_billId(?)}");
                        callSt5.setInt(1, id_update);
                        ResultSet rs4 = callSt5.executeQuery();
                        Bill bill2 = null;
                        while (rs4.next()) {
                            bill2 = new Bill();
                            bill2.setBillId(rs4.getInt("bill_id"));
                            bill2.setBillCode(rs4.getString("bill_code"));
                            bill2.setBillType(rs4.getBoolean("bill_type"));
                            bill2.setEmpIdCreated(rs4.getString("emp_id_created"));
                            bill2.setCreated(rs4.getDate("created"));
                            bill2.setEmpIdAuth(rs4.getString("emp_id_auth"));
                            bill2.setAuthDate(rs4.getDate("auth_date"));
                            bill2.setBillStatus(rs4.getShort("bill_status"));
                        }
                        List<Bill> billList = new ArrayList<>();
                        billList.add(bill2);
                        drawBillTable(billList);
                        List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_update);
                        billDetailList.forEach(System.out::println);
                        callSt = conn.prepareCall("{call get_infor_to_update_bill(?)}");
                        callSt.setInt(1, id_update);
                        ResultSet rs1 = callSt.executeQuery();
                        Bill bill = new Bill();
                        bill.setBillId(id_update);
                        System.out.println("Nhập mã chi tiết phiếu để cập nhật:");
                        int idBillDetail = ExceptionMethod.validateInteger(scanner);
                        callSt6 = conn.prepareCall("{call check_infor_to_update_bill_detail(?,?,?)}");
                        callSt6.setInt(1, idBillDetail);
                        callSt6.setInt(2, id_update);
                        callSt6.registerOutParameter(3, Types.INTEGER);
                        callSt6.execute();
                        int cnt_detailBill = callSt6.getInt(3);
                        if (cnt_detailBill > 0) {
                            callSt2 = conn.prepareCall("{call get_infor_to_update_bill_detail(?,?)}");
                            callSt2.setInt(1, idBillDetail);
                            callSt2.setInt(2, id_update);
                            ResultSet rs2 = callSt2.executeQuery();
                            Bill_Detail billDetail = new Bill_Detail();
                            billDetail.setBillDetailId(idBillDetail);
                            billDetail.setBillId(id_update);
                            do {
                                System.out.println("MENU UPDATE BILL:");
                                System.out.println("1. Mã nhân viên\n2. Trạng thái phiếu\n3. Số lượng sản phẩm\n4. Giá sản phẩm\n5. Thoát");
                                int choice = ExceptionMethod.validateInteger(scanner);
                                boolean resultUpdateBill, resultUpdateBillDetail;
                                switch (choice) {
                                    case 1:
                                        bill.setEmpIdCreated(bill.inputEmpIdCreated(scanner, EmployeePresentation.employeeList));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 2:
                                        bill.setBillStatus(bill.inputBillStatus(scanner));
                                        if (rs1.next()) {
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 3:
                                        billDetail.setQuantity(billDetail.inputQuantity(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 4:
                                        billDetail.setPrice(billDetail.inputPrice(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                        }
                                        resultUpdateBill = updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                    case 5:
                                        ConnectionDB.closeConnection(conn);
                                        BillPresentation.menuBill(scanner);
                                        break;
                                    default:
                                        System.err.println("Mời nhập từ 1 đến 5!");
                                        break;
                                }
                            } while (true);
                        } else {
                            System.err.println("Mã chi tiết phiếu không đúng!");
                        }
                    } else {
                        System.err.println("Phiếu đã duyệt không thể cập nhật!");
                    }
                } else {
                    System.err.println("Không tồn tại mã phiếu xuất! Ấn 3 để nhập lại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 3 để nhập lại!");
        }
    }


    public String resultNotification(boolean result1, boolean result2, String types) {
        if (result1 && result2) {
            return types + " thành công!";
        } else {
            return "Có lỗi trong quá trình thực hiện!";
        }
    }

    public void detailBill(Scanner scanner, List<Bill> billList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        displayBill(billList);
        System.out.println("Nhập mã phiếu hoặc mã code muốn xem chi tiết phiếu:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            if (checkId(input_check) > 0) {
                try {
                    callSt = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt.setString(1, input_check);
                    ResultSet rs = callSt.executeQuery();
                    int id_bill = 0;
                    if (rs.next()) {
                        id_bill = rs.getInt(1);
                    }
                    List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_bill);
                    billDetailBusiness.drawBillDetailTable(billDetailList);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Mã phiếu không tại! Ấn 4 để nhập lại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 4 để nhập lại!");
        }
    }

    public void confirmBill(Scanner scanner, List<Bill> billList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        CallableStatement callSt3 = null;
        CallableStatement callSt4 = null;
        displayBill(billList);
        System.out.println("Nhập mã phiếu hoặc mã code nhập để duyệt:");
        String inputConfirm = ExceptionMethod.StringNotEmpty(scanner);
        if (inputConfirm.length() <= 10) {
            if (checkId(inputConfirm) > 0) {
                try {
                    callSt2 = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt2.setString(1, inputConfirm);
                    ResultSet rs = callSt2.executeQuery();
                    int id_bill = 0;
                    if (rs.next()) {
                        id_bill = rs.getInt(1);
                    }

                    callSt = conn.prepareCall("{call get_bill_status_by_id(?)}");
                    callSt.setInt(1, id_bill);
                    ResultSet resultSet = callSt.executeQuery();
                    short check_status = 0;
                    if (resultSet.next()) {
                        check_status = resultSet.getShort(1);
                    }
                    if (check_status == 0) {
                        System.out.println("Bạn chắc chắn muốn duyệt không?\n1. Duyệt\n2. Thoát");
                        do {
                            int choice = ExceptionMethod.validateInteger(scanner);
                            switch (choice) {
                                case 1:
                                    callSt4 = conn.prepareCall("{call compare_quantity(?,?)}");
                                    callSt4.setInt(1, id_bill);
                                    callSt4.registerOutParameter(2, Types.INTEGER);
                                    callSt4.execute();
                                    int compare = callSt4.getInt(2);
                                    if (compare == 1) {
                                        Bill bill = new Bill();
                                        bill.setBillId(id_bill);
                                        boolean updateStatus = updateStatus(bill);
                                        if (updateStatus) {
                                            conn.setAutoCommit(false);
                                            callSt3 = conn.prepareCall("{call minus_quantity_product(?)}");
                                            callSt3.setInt(1, id_bill);
                                            callSt3.executeUpdate();
                                            conn.commit();
                                            System.out.println(ConsoleColors.makeColor("Duyệt thành công!",ConsoleColors.GREEN));
                                        } else {
                                            System.err.println("Duyệt thất bại!");
                                        }
                                    } else {
                                        System.err.println("Số lượng xuất không được lớn hơn số lượng trong kho!");
                                    }
                                    BillPresentation.menuBill(scanner);
                                    break;
                                case 2:
                                    BillPresentation.menuBill(scanner);
                                    break;
                                default:
                                    System.err.println("Chỉ nhập giá trị 1 và 2!");
                                    break;
                            }
                        } while (true);
                    } else {
                        System.err.println("Trạng thái phiếu là tạo mới được duyệt!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Mã phiếu không tại! Ấn 5 để nhập lại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 5 để nhập lại!");
        }

    }

    public void searchBill(Scanner scanner, List<Bill> billList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        CallableStatement callSt3 = null;
        CallableStatement callSt4 = null;
        CallableStatement callSt5 = null;
        CallableStatement callSt6 = null;
        CallableStatement callSt7 = null;
        CallableStatement callSt8 = null;
        CallableStatement callSt9 = null;
        CallableStatement callSt10 = null;
        System.out.println("Nhập từ khóa mã phiếu hoặc mã code muốn tìm kiếm:");
        String input_search = ExceptionMethod.StringNotEmpty(scanner);
        if (input_search.length() <= 10) {
            billList = search(input_search);
            if (billList != null) {
                drawBillTable(billList);
                System.out.println("Bạn muốn cập nhật hay duyệt phiếu nhập không?\n1. Cập nhật\n2. Duyệt\n3. Thoát");
                do {
                    int choice = ExceptionMethod.validateInteger(scanner);
                    switch (choice) {
                        case 1:
                            System.out.println("Nhập mã phiếu hoặc mã code cần cập nhật từ những phiếu nhập trên:");
                            String input_check = ExceptionMethod.StringNotEmpty(scanner);
                            if (input_check.length() <= 10) {
                                try {
                                    callSt4 = conn.prepareCall("{call check_bill_by_id_bill_or_code_bill(?,?,?)}");
                                    callSt4.setString(1, input_check);
                                    callSt4.setBoolean(2, false);
                                    callSt4.registerOutParameter(3, Types.INTEGER);
                                    callSt4.execute();
                                    int cnt_Bill = callSt4.getInt(3);
                                    if (cnt_Bill > 0) {
                                        callSt3 = conn.prepareCall("{call get_id_bill_by_id_bill_or_code_bill(?,?)}");
                                        callSt3.setString(1, input_check);
                                        callSt3.setBoolean(2, false);
                                        ResultSet rs = callSt3.executeQuery();
                                        int id_update = 0;
                                        if (rs.next()) {
                                            id_update = rs.getInt(1);
                                        }
                                        callSt6 = conn.prepareCall("{call get_status_bill_by_id(?)}");
                                        callSt6.setInt(1, id_update);
                                        ResultSet rs3 = callSt6.executeQuery();
                                        short status = 0;
                                        if (rs3.next()) {
                                            status = rs3.getShort(1);
                                        }
                                        if (status != 2) {
                                            callSt9 = conn.prepareCall("{call get_infor_bill_by_billId(?)}");
                                            callSt9.setInt(1, id_update);
                                            ResultSet rs4 = callSt9.executeQuery();
                                            Bill bill2 = null;
                                            while (rs4.next()) {
                                                bill2 = new Bill();
                                                bill2.setBillId(rs4.getInt("bill_id"));
                                                bill2.setBillCode(rs4.getString("bill_code"));
                                                bill2.setBillType(rs4.getBoolean("bill_type"));
                                                bill2.setEmpIdCreated(rs4.getString("emp_id_created"));
                                                bill2.setCreated(rs4.getDate("created"));
                                                bill2.setEmpIdAuth(rs4.getString("emp_id_auth"));
                                                bill2.setAuthDate(rs4.getDate("auth_date"));
                                                bill2.setBillStatus(rs4.getShort("bill_status"));
                                            }
                                            List<Bill> billList1 = new ArrayList<>();
                                            billList1.add(bill2);
                                            drawBillTable(billList1);
                                            List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_update);
                                            billDetailBusiness.drawBillDetailTable(billDetailList);
                                            callSt = conn.prepareCall("{call get_infor_to_update_bill(?)}");
                                            callSt.setInt(1, id_update);
                                            ResultSet rs1 = callSt.executeQuery();
                                            Bill bill = new Bill();
                                            bill.setBillId(id_update);
                                            System.out.println("Nhập mã chi tiết phiếu để cập nhật:");
                                            int idBillDetail = ExceptionMethod.validateInteger(scanner);
                                            callSt10 = conn.prepareCall("{call check_infor_to_update_bill_detail(?,?,?)}");
                                            callSt10.setInt(1, idBillDetail);
                                            callSt10.setInt(2, id_update);
                                            callSt10.registerOutParameter(3, Types.INTEGER);
                                            callSt10.execute();
                                            int cnt_detailBill = callSt10.getInt(3);
                                            if (cnt_detailBill > 0) {
                                                callSt2 = conn.prepareCall("{call get_infor_to_update_bill_detail(?,?)}");
                                                callSt2.setInt(1, id_update);
                                                callSt2.setInt(2, id_update);
                                                ResultSet rs2 = callSt2.executeQuery();
                                                Bill_Detail billDetail = new Bill_Detail();
                                                billDetail.setBillDetailId(idBillDetail);
                                                billDetail.setBillId(id_update);
                                                do {
                                                    System.out.println("MENU UPDATE RECEIPT");
                                                    System.out.println("1. Mã nhân viên\n2. Trạng thái phiếu\n3. Số lượng sản phẩm\n4. Giá sản phẩm\n5. Thoát");
                                                    int choice2 = ExceptionMethod.validateInteger(scanner);
                                                    boolean resultUpdateBill, resultUpdateBillDetail;
                                                    switch (choice2) {
                                                        case 1:
                                                            bill.setEmpIdCreated(bill.inputEmpIdCreated(scanner, EmployeePresentation.employeeList));
                                                            if (rs1.next()) {
                                                                bill.setBillStatus(rs1.getShort("bill_status"));
                                                            }
                                                            if (rs2.next()) {
                                                                billDetail.setQuantity(rs2.getInt("quantity"));
                                                                billDetail.setPrice(rs2.getFloat("price"));
                                                            }
                                                            resultUpdateBill = updateInfor(bill);
                                                            resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                                            break;
                                                        case 2:
                                                            bill.setBillStatus(bill.inputBillStatus(scanner));
                                                            if (rs1.next()) {
                                                                bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                                            }
                                                            if (rs2.next()) {
                                                                billDetail.setQuantity(rs2.getInt("quantity"));
                                                                billDetail.setPrice(rs2.getFloat("price"));
                                                            }
                                                            resultUpdateBill = updateInfor(bill);
                                                            resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                                            break;
                                                        case 3:
                                                            billDetail.setQuantity(billDetail.inputQuantity(scanner));
                                                            if (rs1.next()) {
                                                                bill.setBillStatus(rs1.getShort("bill_status"));
                                                                bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                                            }
                                                            if (rs2.next()) {
                                                                billDetail.setPrice(rs2.getFloat("price"));
                                                            }
                                                            resultUpdateBill = updateInfor(bill);
                                                            resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                                            break;
                                                        case 4:
                                                            billDetail.setPrice(billDetail.inputPrice(scanner));
                                                            if (rs1.next()) {
                                                                bill.setBillStatus(rs1.getShort("bill_status"));
                                                                bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                                            }
                                                            if (rs2.next()) {
                                                                billDetail.setQuantity(rs2.getInt("quantity"));
                                                            }
                                                            resultUpdateBill = updateInfor(bill);
                                                            resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                                        case 5:
                                                            ConnectionDB.closeConnection(conn);
                                                            BillPresentation.menuBill(scanner);
                                                            break;
                                                        default:
                                                            System.err.println("Mời nhập từ 1 đến 5!");
                                                            break;
                                                    }
                                                } while (true);
                                            } else {
                                                System.err.println("Mã chi tiết phiếu không đúng!");
                                                BillPresentation.menuBill(scanner);
                                            }
                                        } else {
                                            System.err.println("Phiếu đã duyệt không được sửa!");
                                            BillPresentation.menuBill(scanner);
                                        }
                                    } else {
                                        System.err.println("Không tồn tại mã phiếu xuất trên những phiếu tìm kiếm!");
                                        BillPresentation.menuBill(scanner);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                System.err.println("Tối đa 10 ký tự!");
                                BillPresentation.menuBill(scanner);
                            }
                            break;
                        case 2:
                            System.out.println("Nhập mã phiếu hoặc mã code nhập để duyệt:");
                            String inputConfirm = ExceptionMethod.StringNotEmpty(scanner);
                            if (inputConfirm.length() <= 10) {
                                if (checkId(inputConfirm) > 0) {
                                    try {
                                        callSt5 = conn.prepareCall("{call get_id_bill_by_id_bill_or_code_bill(?,?)}");
                                        callSt5.setString(1, inputConfirm);
                                        callSt5.setBoolean(2, false);
                                        ResultSet rs = callSt5.executeQuery();
                                        int id_bill = 0;
                                        if (rs.next()) {
                                            id_bill = rs.getInt(1);
                                        }
                                        callSt = conn.prepareCall("{call get_bill_status_by_id(?)}");
                                        callSt.setInt(1, id_bill);
                                        ResultSet resultSet = callSt.executeQuery();
                                        short check_status = 0;
                                        if (resultSet.next()) {
                                            check_status = resultSet.getShort(1);
                                        }
                                        if (check_status == 0) {
                                            System.out.println("Bạn chắc chắn muốn duyệt không?\n1. Duyệt\n2. Thoát");
                                            do {
                                                int choice3 = ExceptionMethod.validateInteger(scanner);
                                                switch (choice3) {
                                                    case 1:
                                                        Bill bill = new Bill();
                                                        bill.setBillId(id_bill);
                                                        boolean updateStatus = updateStatus(bill);
                                                        callSt8 = conn.prepareCall("{call compare_quantity(?,?)}");
                                                        callSt8.setInt(1, id_bill);
                                                        callSt8.registerOutParameter(2, Types.INTEGER);
                                                        callSt8.execute();
                                                        int compare = callSt8.getInt(2);
                                                        if (compare == 1) {
                                                            if (updateStatus) {
                                                                conn.setAutoCommit(false);
                                                                callSt7 = conn.prepareCall("{call minus_quantity_product(?)}");
                                                                callSt7.setInt(1, id_bill);
                                                                callSt7.executeUpdate();
                                                                conn.commit();
                                                                System.out.println(ConsoleColors.makeColor("Duyệt thành công!",ConsoleColors.GREEN));
                                                            } else {
                                                                System.err.println("Duyệt thất bại!");
                                                            }
                                                        } else {

                                                        }
                                                        BillPresentation.menuBill(scanner);
                                                        break;
                                                    case 2:
                                                        BillPresentation.menuBill(scanner);
                                                        break;
                                                    default:
                                                        System.err.println("Chỉ nhập giá trị 1 và 2!");
                                                        break;
                                                }
                                            } while (true);
                                        } else {
                                            System.err.println("Trạng thái phiếu là tạo mới được duyệt!");
                                            BillPresentation.menuBill(scanner);
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    System.err.println("Mã phiếu không tại trên những phiếu tìm kiếm!");
                                    BillPresentation.menuBill(scanner);
                                }
                            } else {
                                System.err.println("Tối đa 10 ký tự!");
                                BillPresentation.menuBill(scanner);
                            }
                            break;
                        case 3:
                            BillPresentation.menuBill(scanner);
                            break;
                        default:
                            System.err.println("Mời bạn nhập từ 1 đến 3 !");
                            break;
                    }
                } while (true);
            } else {
                System.err.println("Thông tin về phiếu xuất bạn nhập không tồn tại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 6 để nhập lại!");
        }
    }

    public void drawBillTable(List<Bill> billList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Connection conn = null;

        try {
            conn = ConnectionDB.openConnection();
            CallableStatement callSt = conn.prepareCall("{call get_name_employee_by_id_employee(?)}");

            System.out.println("                                            Bảng thông tin phiếu xuất                          ");
            for (int i = 0; i < 136; i++) {
                System.out.print("─");
            }
            System.out.println();
            System.out.printf("|%-10s|%-15s|%-12s|%-20s|%-15s|%-20s|%-15s|%-20s|%n",
                    "Mã phiếu",
                    "Mã code phiếu",
                    "Loại phiếu",
                    "Người tạo",
                    "Ngày tạo",
                    "Người duyệt",
                    "Ngày duyệt",
                    "Trạng thái phiếu");
            for (int i = 0; i < 136; i++) {
                System.out.print("─");
            }
            System.out.println();

            for (Bill bill : billList) {
                callSt.setString(1, bill.getEmpIdCreated());
                ResultSet rs1 = callSt.executeQuery();
                String nameEmpCreated = null;
                if (rs1.next()) {
                    nameEmpCreated = rs1.getString(1);
                }
                callSt.setString(1, bill.getEmpIdAuth());
                ResultSet rs2 = callSt.executeQuery();
                String nameEmpAuth = null;
                if (rs2.next()) {
                    nameEmpAuth = rs2.getString(1);
                }
                System.out.printf("|%-10s|%-15s|%-12s|%-20s|%-15s|%-20s|%-15s|%-20s|%n",
                        bill.getBillId(),
                        bill.getBillCode(),
                        (bill.getBillType() ? "Phiếu nhập" : "Phiếu xuất"),
                        (nameEmpCreated == null ? "" : nameEmpCreated),
                        (bill.getCreated() == null ? "" : sdf.format(bill.getCreated())),
                        (nameEmpAuth == null ? "" : nameEmpAuth),
                        (bill.getAuthDate() == null ? " " : sdf.format(bill.getAuthDate())),
                        (bill.getBillStatus() == 0) ? "Tạo" : bill.getBillStatus() == 1 ? "Hủy" : "Duyệt");

                for (int i = 0; i < 136; i++) {
                    System.out.print("─");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }
}
