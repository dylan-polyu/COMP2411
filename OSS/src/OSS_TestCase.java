import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

// ADD JUNIT TO CLASS PATH IS REQUIRED

public class OSS_TestCase {


    // EACH TEST IS INDEPENDENT. PLEASE RUN INDIVIDUALLY TO PREVENT TRIGGERING OUR CONCURRENCY EXCEPTION (AS ONLY 3 USERS ARE ALLOWED AT A TIME)


    @Test
    public void testUserExist() throws SQLException {
        //Test query user exist or not
        boolean queryResult = OSS.userExist("test1");
        assertTrue("User Exists", queryResult);
    }


    @Test
    public void testGetUserPwd() throws SQLException {
        // Test for user password
        String pwd = OSS.userPwd("test1");
        assertEquals("test", pwd);
    }


    @Test
    public void testAdminExist() throws SQLException {
        // Test successful product search
        boolean queryResult = OSS.adminExist("rai");
        assertTrue("Admin Exists", queryResult);
    }


    @Test
    public void testSelectAdminPwd() throws SQLException {
        // Test query admin exist or not
        String pwd = OSS.selectAdminPwd("rai");
        assertEquals("rai", pwd);
    }


    @Test
    public void testCreateUser() throws SQLException {
        // Test create a user
        boolean addResult = OSS.createUser("testUser", "test","test","test",
                "2023-12-2","test@test.com","1234578","testing address");
        assertTrue("Added a user successfully", addResult);
    }


    @Test
    public void testCreateAdmin() throws SQLException {
        // Test create a admin
        boolean addResult = OSS.createAdmin("tester7", "test","test","test",
                "2023-12-02","testA@test.com","12345678");
        assertTrue("Added an admin successfully", addResult);
    }


    @Test
    public void testSelectReport() throws SQLException {
        // Test query report
        List<Map<String, Object>> resultList = OSS.selectReport("VIEWS", true);
        assertNotNull(resultList);
    }


    @Test
    public void testSelectReportUnion() throws SQLException {
        // Test query report union info
        List<Map<String, Object>> resultList = OSS.selectReportUnion();
        assertNotNull(resultList);
    }


    @Test
    public void testSelectProductBylike() throws SQLException {
        // Test query products by keyword
        List<Map<String, Object>> resultList = OSS.selectProductBylike("chicken");
        assertNotNull(resultList);
    }


    @Test
    public void testSelectAllProduct() throws SQLException {
        // Test query all products
        List<Map<String, Object>> resultList = OSS.selectAllProduct();
        assertNotNull(resultList);
    }


    @Test
    public void testSelectProductById() throws SQLException {
        // Test query product by id
        Map<String, Object> resultList = OSS.selectProductById("1");
        assertNull(resultList);
    }


    @Test
    public void testSelectStockQtyById() throws SQLException {
        // Test query product stock
        int stock = OSS.selectStockQtyById("chicken");
        assertEquals(936, stock);
    }


    @Test
    public void testAddOrUpdateCart() throws SQLException {
        // Test add or update to cart
        String operation = OSS.addOrUpdateCart(1, "test", "chicken");
        assertEquals("add", operation);
    }


    @Test
    public void testdeleteFromCart() throws SQLException {
        // Test add or update to cart
        String testProduct = OSS.deleteFromCart( "test","chicken");
        assertEquals("chicken", testProduct);
    }




    @Test
    public void testSelectCartByUserId() throws SQLException {
        // Test query cart by userId
        List<Map<String, Object>> resultList = OSS.selectCartByUserId( "test");
        assertNotNull(resultList);
    }
}

