package db;

import java.util.List;

import db.ProductDbManager;
import models.Product;

/**
 * ProductDAO 類別 - 資料存取物件模式實現
 * 
 * DAO (Data Access Object) 模式詳解:
 * 
 * 1. 定義與目的:
 *    DAO是一種設計模式，用於將資料存取邏輯與業務邏輯分離。
 *    它提供了一個抽象接口來操作資料庫，而無需暴露資料庫的細節。
 * 
 * 2. DAO模式的主要組件:
 *    - DAO接口: 定義資料操作的方法 (例如增、刪、改、查)
 *    - DAO實現類: 實現DAO接口，包含具體的資料庫操作代碼
 *    - 模型/實體對象: 表示資料結構的Java對象 (例如Product類)
 *    - 資料來源: 資料庫連接器或其他資料來源 (例如DatabaseConnection類)
 * 
 * 3. DAO模式的優點:
 *    - 分離關注點: 業務邏輯與資料存取邏輯分開
 *    - 提高可維護性: 資料庫實現變化時，只需修改DAO實現
 *    - 提升可測試性: 可使用mock對象替代真實的DAO進行測試
 *    - 靈活性: 可以輕鬆切換不同的資料存儲技術 (如SQL、NoSQL、ORM等)
 *    - 代碼重用: 避免在多個地方重複編寫相同的資料庫存取代碼
 * 
 * 4. DAO與Repository模式的區別:
 *    - DAO專注於資料持久化的基本CRUD操作
 *    - Repository專注於提供領域對象的集合視圖，通常建立在DAO之上
 * 
 * 5. 在本應用中的實現:
 *    - 本類(ProductDAO)作為DAO層，提供簡潔的資料操作接口
 *    - ProductDatabaseManager負責實際的SQL和資料庫連接
 *    - 應用的其他部分(如UI或控制器)只需知道ProductDAO的接口，不需了解資料庫細節
 */
public class ProductDAO {
    private ProductDbManager dbManager;
    
    public ProductDAO() {
        dbManager = new ProductDbManager();
    }
    
    public boolean add(Product product) {
        return dbManager.insertProduct(product);
    }
    
    public boolean update(Product product) {
        return dbManager.updateProduct(product);
    }
    
    public boolean delete(String productId) {
        return dbManager.deleteProduct(productId);
    }
    
    public Product getProductById(String productId) {
        return dbManager.getProductById(productId);
    }
    
    public List<Product> getAllProducts() {
        return dbManager.getAllProducts();
    }

    public Product getProductsById(String id) {
        return dbManager.getProductById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return dbManager.getProductsByCategory(category);
    }
    
    public List<Product> getProductsByName(String name) {
        return dbManager.getProductsByName(name);
    }
    public void closeConnection() {
        dbManager.close();
    }
}
