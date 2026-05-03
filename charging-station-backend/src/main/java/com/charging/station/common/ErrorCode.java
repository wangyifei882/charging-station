package com.charging.station.common;

public enum ErrorCode {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或Token失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器内部错误"),
    LOGIN_FAILED(1001, "用户名或密码错误"),
    ACCOUNT_DISABLED(1002, "账号已被禁用"),
    DEVICE_OFFLINE(2001, "设备不在线"),
    DEVICE_CHARGING(2002, "设备正在充电中"),
    ORDER_PAID(3001, "订单已支付"),
    ORDER_NOT_REFUNDABLE(3002, "订单不可退款"),
    CONTROL_FAILED(4001, "设备控制失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
