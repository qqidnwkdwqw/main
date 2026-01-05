package labSystem.entity;
import java.util.Date;

/**
 * 设备实体类
 * 状态：available(可用), in_use(使用中), maintenance(维修中), 
 *       scrapped(已报废), reserved(已预约)
 */
public class Device {
    private Integer deviceId;
    private String deviceCode;      // 设备编号（唯一）
    private String deviceName;
    private Integer categoryId;     // 分类ID
    private String categoryName;    // 分类名称（关联查询）
    private String model;           // 型号
    private String brand;           // 品牌
    private String specifications;  // 规格参数
    private String location;        // 存放位置
    private String status;          // 设备状态
    private String description;     // 设备描述
    private Date purchaseDate;      // 购买日期
    private Double price;           // 价格
    private Integer warrantyMonths; // 保修期（月）
    private Integer managerId;      // 管理员ID
    private String managerName;     // 管理员姓名（关联查询）
    private Integer totalUsageCount;// 总使用次数
    private Double totalUsageHours; // 总使用时长（小时）
    private Boolean isDeleted;      // 是否软删除
    private Date createdAt;
    private Date updatedAt;
    
    // === 状态检查方法 ===
    public boolean isAvailable() {
        return "available".equals(status);
    }
    
    public boolean isInUse() {
        return "in_use".equals(status);
    }
    
    public boolean isUnderMaintenance() {
        return "maintenance".equals(status);
    }
    
    public boolean isScrapped() {
        return "scrapped".equals(status);
    }
    
    public boolean isReserved() {
        return "reserved".equals(status);
    }
    
    public boolean canBeReserved() {
        return isAvailable() || isReserved();
    }
    
    public boolean isOperational() {
        return isAvailable() || isInUse() || isReserved();
    }
    
    // === 构造方法 ===
    public Device() {
        this.status = "available";
        this.isDeleted = false;
        this.totalUsageCount = 0;
        this.totalUsageHours = 0.0;
    }
    
    public Device(String deviceCode, String deviceName, Integer categoryId, String location) {
        this();
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.categoryId = categoryId;
        this.location = location;
    }
    
    public Device(String deviceCode, String deviceName, String model, String brand, 
                  String location, String status) {
        this();
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.model = model;
        this.brand = brand;
        this.location = location;
        this.status = status;
    }
    
    // === Getter和Setter ===
    public Integer getDeviceId() { 
        return deviceId; 
    }
    
    public void setDeviceId(Integer deviceId) { 
        this.deviceId = deviceId; 
    }
    
    public String getDeviceCode() { 
        return deviceCode; 
    }
    
    public void setDeviceCode(String deviceCode) { 
        this.deviceCode = deviceCode; 
    }
    
    public String getDeviceName() { 
        return deviceName; 
    }
    
    public void setDeviceName(String deviceName) { 
        this.deviceName = deviceName; 
    }
    
    public Integer getCategoryId() { 
        return categoryId; 
    }
    
    public void setCategoryId(Integer categoryId) { 
        this.categoryId = categoryId; 
    }
    
    public String getCategoryName() { 
        return categoryName; 
    }
    
    public void setCategoryName(String categoryName) { 
        this.categoryName = categoryName; 
    }
    
    public String getModel() { 
        return model; 
    }
    
    public void setModel(String model) { 
        this.model = model; 
    }
    
    public String getBrand() { 
        return brand; 
    }
    
    public void setBrand(String brand) { 
        this.brand = brand; 
    }
    
    public String getSpecifications() { 
        return specifications; 
    }
    
    public void setSpecifications(String specifications) { 
        this.specifications = specifications; 
    }
    
    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public Date getPurchaseDate() { 
        return purchaseDate; 
    }
    
    public void setPurchaseDate(Date purchaseDate) { 
        this.purchaseDate = purchaseDate; 
    }
    
    public Double getPrice() { 
        return price; 
    }
    
    public void setPrice(Double price) { 
        this.price = price; 
    }
    
    public Integer getWarrantyMonths() { 
        return warrantyMonths; 
    }
    
    public void setWarrantyMonths(Integer warrantyMonths) { 
        this.warrantyMonths = warrantyMonths; 
    }
    
    public Integer getManagerId() { 
        return managerId; 
    }
    
    public void setManagerId(Integer managerId) { 
        this.managerId = managerId; 
    }
    
    public String getManagerName() { 
        return managerName; 
    }
    
    public void setManagerName(String managerName) { 
        this.managerName = managerName; 
    }
    
    public Integer getTotalUsageCount() { 
        return totalUsageCount; 
    }
    
    public void setTotalUsageCount(Integer totalUsageCount) { 
        this.totalUsageCount = totalUsageCount; 
    }
    
    public Double getTotalUsageHours() { 
        return totalUsageHours; 
    }
    
    public void setTotalUsageHours(Double totalUsageHours) { 
        this.totalUsageHours = totalUsageHours; 
    }
    
    public Boolean getIsDeleted() { 
        return isDeleted; 
    }
    
    public void setIsDeleted(Boolean isDeleted) { 
        this.isDeleted = isDeleted; 
    }
    
    public Date getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Date createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public Date getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(Date updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    // === 显示方法 ===
    public String getStatusDisplayName() {
        switch (status) {
            case "available": return "可用";
            case "in_use": return "使用中";
            case "maintenance": return "维修中";
            case "scrapped": return "已报废";
            case "reserved": return "已预约";
            default: return status;
        }
    }
    
    public String getStatusColor() {
        switch (status) {
            case "available": return "green";
            case "in_use": return "blue";
            case "maintenance": return "orange";
            case "scrapped": return "gray";
            case "reserved": return "yellow";
            default: return "black";
        }
    }
    
    public String getFormattedPrice() {
        if (price == null) return "未记录";
        return String.format("¥%.2f", price);
    }
    
    public String getWarrantyInfo() {
        if (warrantyMonths == null || warrantyMonths <= 0) return "无保修";
        return warrantyMonths + "个月";
    }
    
    public String getUsageInfo() {
        return String.format("使用%d次，累计%.1f小时", 
            totalUsageCount != null ? totalUsageCount : 0,
            totalUsageHours != null ? totalUsageHours : 0.0);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s [%s]", deviceCode, deviceName, getStatusDisplayName());
    }
    
    // === 工具方法 ===
    public String getFullInfo() {
        return String.format("%s (%s %s) - %s", 
            deviceName, brand, model, location);
    }
    
    public boolean isValidForReservation() {
        return !isDeleted && (isAvailable() || isReserved());
    }
}


