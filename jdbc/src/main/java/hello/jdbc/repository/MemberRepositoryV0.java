package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {

        // PreparedStatement의 Parameter Binding 방식을 활용하면 SQL Injection 공격 예방 가능.
        String sql = "INSERT INTO MEMBER(MEMBER_ID, MONEY) VALUES(?, ?)";

        Connection con = null;
        // Statement는 SQL 쿼리를 그대로 지정.
        // PreparedStatement는 SQL 쿼리에서 Parameter 지정 가능.
        // Statement는 PreparedStatement를 상속 받았음.
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            // executeUpdate()는 영향받은 DB row 수만큼 int를 반환해준다.
            pstmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally { // Connection Close 처리를 해주지 않으면 Connection이 끊어지지 않고 계속 유지되는 문제 발생. (리소스 누수)
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        // ResultSet는 조회할 때 사용.
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if(con != null) {
            try {
                con.close();
            } catch(SQLException e) {
                log.info("error", e);
            }
        }

    }

    // DriverManager를 통해서 Connection을 획득.
    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
