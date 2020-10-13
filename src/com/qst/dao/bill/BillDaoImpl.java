package com.qst.dao.bill;

import com.mysql.cj.util.StringUtils;
import com.qst.dao.BaseDao;
import com.qst.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class BillDaoImpl implements BillDao {

	@Override
	public int add(Connection connection, Bill bill) throws Exception {
		PreparedStatement pstm = null;
		int flag = 0;
		if (null != connection) {
			String sql = "insert into smbms_bill (billCode,productName,productDesc," +
					"productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
					"values(?,?,?,?,?,?,?,?,?,?)";
			Object[] params = {bill.getBillCode(), bill.getProductName(), bill.getProductDesc(),
					bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
					bill.getProviderId(), bill.getCreatedBy(), bill.getCreationDate()};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
			System.out.println("dao--------flag " + flag);
		}
		return flag;
	}

	@Override
    public List<Bill> getBillList(Connection connection, String productname, int providerid, int ispayment, int currentPageNo, int pageSize) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Bill> billList = new ArrayList<>();
		if (connection != null) {
			StringBuffer sql = new StringBuffer();
			sql.append("select b.*,p.proName as providerName from smbms_bill b, smbms_provider p where b.providerId = p.id");
			List<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(productname)) {
				sql.append(" and productName like ?");
                list.add("%" + productname + "%");
			}
            if (providerid > 0) {
				sql.append(" and providerId = ?");
                list.add(providerid);
			}
            if (ispayment > 0) {
				sql.append(" and isPayment = ?");
                list.add(ispayment);
			}
            //在数据库中，分页显示 limit startIndex，pageSize；总数
            //当前页  (当前页-1)*页面大小
            //0,5	1,0	 01234
            //5,5	5,0	 56789
            //10,5	10,0 10~
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
			Object[] params = list.toArray();
			System.out.println("sql --------- > " + sql.toString());
			rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
			while (rs.next()) {
				Bill _bill = new Bill();
				_bill.setId(rs.getInt("id"));
				_bill.setBillCode(rs.getString("billCode"));
				_bill.setProductName(rs.getString("productName"));
				_bill.setProductDesc(rs.getString("productDesc"));
				_bill.setProductUnit(rs.getString("productUnit"));
				_bill.setProductCount(rs.getBigDecimal("productCount"));
				_bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
				_bill.setIsPayment(rs.getInt("isPayment"));
				_bill.setProviderId(rs.getInt("providerId"));
				_bill.setProviderName(rs.getString("providerName"));
				_bill.setCreationDate(rs.getTimestamp("creationDate"));
				_bill.setCreatedBy(rs.getInt("createdBy"));
				billList.add(_bill);
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return billList;
	}

	@Override
	public int deleteBillById(Connection connection, String delId) throws Exception {
		PreparedStatement pstm = null;
		int flag = 0;
		if (null != connection) {
			String sql = "delete from smbms_bill where id=?";
			Object[] params = {delId};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return flag;
	}

	@Override
	public Bill getBillById(Connection connection, String id) throws Exception {
		Bill bill = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		if (null != connection) {
			String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
					"where b.providerId = p.id and b.id=?";
			Object[] params = {id};
			rs = BaseDao.execute(connection, pstm, rs, sql, params);
			if (rs.next()) {
				bill = new Bill();
				bill.setId(rs.getInt("id"));
				bill.setBillCode(rs.getString("billCode"));
				bill.setProductName(rs.getString("productName"));
				bill.setProductDesc(rs.getString("productDesc"));
				bill.setProductUnit(rs.getString("productUnit"));
				bill.setProductCount(rs.getBigDecimal("productCount"));
				bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
				bill.setIsPayment(rs.getInt("isPayment"));
				bill.setProviderId(rs.getInt("providerId"));
				bill.setProviderName(rs.getString("providerName"));
				bill.setModifyBy(rs.getInt("modifyBy"));
				bill.setModifyDate(rs.getTimestamp("modifyDate"));
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return bill;
	}

	@Override
	public int modify(Connection connection, Bill bill) throws Exception {
		int flag = 0;
		PreparedStatement pstm = null;
		if (null != connection) {
			String sql = "update smbms_bill set productName=?," +
					"productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
					"isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
			Object[] params = {bill.getProductName(), bill.getProductDesc(),
					bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
					bill.getProviderId(), bill.getModifyBy(), bill.getModifyDate(), bill.getId()};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return flag;
	}

	@Override
	public int getBillCountByProviderId(Connection connection, String providerId) throws Exception {
		int count = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		if (null != connection) {
			String sql = "select count(1) as billCount from smbms_bill where" +
					"	providerId = ?";
			Object[] params = {providerId};
			rs = BaseDao.execute(connection, pstm, rs, sql, params);
			if (rs.next()) {
				count = rs.getInt("billCount");
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return count;
	}

    @Override
    public int getBillCount(Connection connection, String productname, int providerid, int ispayment) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;

        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_bill b, smbms_provider p where b.providerId = p.id");
            List<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(productname)) {
                sql.append(" and productName like ?");
                list.add("%" + productname + "%");
            }
            if (providerid > 0) {
                sql.append(" and providerId = ?");
                list.add(providerid);
            }
            if (ispayment > 0) {
                sql.append(" and isPayment = ?");
                list.add(ispayment);
            }
            //怎么把List 转成数组
            Object[] params = list.toArray();
            //输出最后的完整语句
            System.out.println("BillDaoImpl->getBillCount:" + sql.toString());

            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);

            if (rs.next()) {
                count = rs.getInt("count");//从结果集中获取最终数量

            }
            BaseDao.closeResource(null, pstm, rs);

        }
        return count;
    }
}
