package com.lda.response;

public enum ResultCode implements CustomizeResultCode {
    /* 成功 */
    SUCCESS(200, "成功"),

    /* 默认失败 */
    COMMON_FAIL(999, "失败"),
    /* 没有权限异常 */
    NO_PERMISSION_EXCEPTION(401, "没有权限"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),

    /* 用户错误 */
    USER_NOT_LOGIN(2001, "用户未登录"),
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),
    USER_CREDENTIALS_ERROR(2003, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2004, "密码过期"),
    USER_ACCOUNT_DISABLE(2005, "账号不可用"),
    USER_ACCOUNT_LOCKED(2006, "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST(2007, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2008, "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号下线"),
    USER_ACCOUNT_IS_NOT_UNIQUE(2010, "账号不唯一"),
    CANNOT_DELETE_THE_CURRENT_ACTIVE_ACCOUNT(2012, "不能删除当前活动账户"),
    USER_TOKEN_ERROR(2011, "token错误"),


    /*日志错误*/
    OPERATION_LOG_DOES_NOT_EXIST(6001, "操作日志不存在"),
    LOGIN_LOG_DOES_NOT_EXIST(6002, "登入日志不存在"),
    /*部门错误*/
    DEPARTMENT_NOT_EXIST(3007, "部门不存在"),
    DEPARTMENT_ALREADY_EXIST(3008, "部门已存在"),

    /*角色错误*/
    Role_NOT_EXIST(4007, "角色不存在"),
    Role_ALREADY_EXIST(4008, "角色已存在"),
    /*菜单错误*/
    MENU_NOT_EXIST(4007, "菜单不存在"),
    MENU_ALREADY_EXIST(4008, "菜单已存在"),
    MENU_CANNOT_DELETE(4009, "该节点存在子节点的 不能删除！！"),
    /* 业务错误 */
    NO_PERMISSION(3001, "没有权限"),
    /*物资分类错误*/
    CATEGORIES_NOT_EXIST(8001, "物资分类不存在"),
    CATEGORIES_ALREADY_EXIST(8002, "物资分类已存在"),
    CATEGORIES_CANNOT_DELETE(8009, "该分类存在子分类的 不能删除！！"),
    /*物资错误*/
    PRODUCT_NOT_EXIST(18001, "物资不存在"),
    PRODUCT_ALREADY_EXIST(18002, "物资已存在"),
    PRODUCT_STATUS_ERROR(18003,"物资状态错误"),
    PRODUCT_IS_REMOVE(18004,"物资已移入回收站"),
    PRODUCT_WAIT_PASS(18005,"物资等待审核"),
    PRODUCT_IN_STOCK_NUMBER_ERROR(18006,"物资入库数量非法"),
    PRODUCT_OUT_STOCK_NUMBER_ERROR(18009,"物资出库数量非法"),
    PRODUCT_IN_STOCK_NUMBER_EMPTY(18007,"入库物资不能为空"),
    PRODUCT_OUT_STOCK_NUMBER_EMPTY(18007,"出库物资不能为空"),
  /*入库单错误*/
   WAREHOUSERECEIPT_NOT_EXIST(28001, "入库单不存在"),
   WAREHOUSERECEIPT_ALREADY_EXIST(28002, "入库单已存在"),
   WAREHOUSERECEIPT_STATUS_ERROR(28003,"入库单状态错误"),
   WAREHOUSERECEIPT_IS_REMOVE(28004,"入库单已移入回收站"),
   WAREHOUSERECEIPT_WAIT_PASS(28005,"入库单等待审核"),

    /*物资库存错误*/
    Material_inventory_NOT_EXIST(290001,"物资库存不存在"),
    INSUFFICIENT_INVENTORY_OF_MATERIALS(290002,"物资库存不足"),

    /*出库单错误*/
    OUTBOUND_ORDER_NOT_EXIST(38001, "出库单不存在"),
    OUTBOUND_ORDER_ALREADY_EXIST(38002, "出库单已存在"),
    OUTBOUND_ORDER_STATUS_ERROR(38003,"出库单状态错误"),
    OUTBOUND_ORDER_IS_REMOVE(38004,"出库单已移入回收站"),
    OUTBOUND_ORDER_WAIT_PASS(38005,"出库单等待审核"),
    /*出库单信息错误*/
    OUTBOUND_ORDER_INFORMATION_NOT_EXIST(38001, "出库单不存在"),
    /*物资来源错误*/
    MATERIAL_SOURCE_NOT_EXIST(59001, "物资提供方不存在"),
    MATERIAL_SOURCE_ALREADY_EXIST(59002, "物资提供方已存在"),
    MATERIAL_SOURCE_STATUS_ERROR(59003,"物资提供方状态错误"),
    /*物资去向错误*/
    WHERE_TO_GO_NOT_EXIST(19001, "物资收取方不存在"),
    WHERE_TO_GO_ALREADY_EXIST(19002, "物资收取方已存在"),
    WHERE_TO_GO_STATUS_ERROR(19003,"物资收取方状态错误"),
    /*运行时异常*/
    ARITHMETIC_EXCEPTION(9001,"算数异常");

    private Integer code;

    private String message;

    ResultCode(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
