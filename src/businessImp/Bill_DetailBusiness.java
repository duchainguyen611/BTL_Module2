package businessImp;

import entity.Bill_Detail;
import util.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bill_DetailBusiness {

    public List<Bill_Detail> findAll(int id_bill) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Bill_Detail> billDetailList = null;
        try {
            callSt = conn.prepareCall("{call get_infor_bill_detail(?)}");
            callSt.setInt(1,id_bill);
            ResultSet rs = callSt.executeQuery();
            billDetailList = new ArrayList<>();
            while (rs.next()) {
                Bill_Detail billDetail = new Bill_Detail();
                billDetail.setBillDetailId(rs.getInt("bill_detail_id"));
                billDetail.setBillId(rs.getInt("bill_id"));
                billDetail.setProductId(rs.getString("product_id"));
                billDetail.setQuantity(rs.getInt("quantity"));
                billDetail.setPrice(rs.getFloat("price"));
                billDetailList.add(billDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return billDetailList;
    }


    public boolean create(Bill_Detail billDetail) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call add_infor_bill_detail(?,?,?,?)}");
            callSt.setInt(1,billDetail.getBillId() );
            callSt.setString(2, billDetail.getProductId());
            callSt.setInt(3, billDetail.getQuantity());
            callSt.setFloat(4,billDetail.getPrice());
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

    public boolean updateInfor(Bill_Detail billDetail) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_infor_bill_detail(?,?,?,?)}");
            callSt.setInt(1,billDetail.getBillDetailId());
            callSt.setInt(2,billDetail.getBillId());
            callSt.setInt(3,billDetail.getQuantity());
            callSt.setFloat(4,billDetail.getPrice());
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

    public void drawBillDetailTable(List<Bill_Detail> billDetailList) {
        Connection conn = ConnectionDB.openConnection();
        try {
            CallableStatement callSt = conn.prepareCall("{call get_product_name_by_product_id(?)}");
            System.out.println("                                       Bảng thông tin chi tiết hóa đơn                          ");
            for (int i = 0; i < 90; i++) {
                System.out.print("─");
            }
            System.out.println();
            System.out.printf("|%-20s|%-15s|%-20s|%-15s|%-15s|%n",
                    "Mã chi tiết phiếu",
                    "Mã hóa đơn",
                    "Tên sản phẩm",
                    "Số lượng",
                    "Đơn giá");
            for (int i = 0; i < 90; i++) {
                System.out.print("─");
            }
            System.out.println();
            for (Bill_Detail billDetail : billDetailList) {
                callSt.setString(1,billDetail.getProductId());
                ResultSet rs = callSt.executeQuery();
                String nameProduct = null;
                if (rs.next()){
                    nameProduct = rs.getString(1);
                }
                System.out.printf("|%-20s|%-15s|%-20s|%-15s|%-15s|%n",
                        billDetail.getBillDetailId(),
                        billDetail.getBillId(),
                        nameProduct,
                        billDetail.getQuantity(),
                        billDetail.getPrice());
                for (int i = 0; i < 90; i++) {
                    System.out.print("─");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
    }
}
