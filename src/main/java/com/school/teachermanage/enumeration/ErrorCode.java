package com.school.teachermanage.enumeration;

/**
 * 返回码枚举
 *
 * @author zhangsl
 * @date 2017-11-05
 */
public enum ErrorCode {
    SUCCESS("0000", "操作成功"),
    USER_EXIST("0001", "用户已存在"),
    USER_UNREGISTER("0002", "用户未注册"),
    MOBILE_CODE_ERROR("0003", "手机验证码错误"),
    VERIFY_CODE_ERROR("0004", "图片验证码错误"),
    MOBILE_NULL("0005", "手机号为空"),
    PASSWORD_NULL("0006", "密码为空"),
    MOBILE_FORMAT_ERROR("0007", "手机号格式错误"),
    UP_USER_NOT_EXIST("0008", "上级不存在"),
    PASSWORD_ERROR("0009", "密码错误"),
    TOKEN_ERROR("0010", "令牌错误"),
    LOGIN_NAME_NULL("0011", "登录名称为空"),
    USER_NOT_EXIST("0012", "用户不存在"),
    PASSWORD_FORMAT_ERROR("0013", "密码格式错误"),
    DATE_FORMAT_ERROR("0014", "日期格式错误"),
    DATE_TIME_FORMAT_ERROR("0015", "时间格式错误"),
    CONVERT_PERCENT_ERROR("0016", "比率值错误"),
    CONVERT_LEVEL_MULTIPLE("0017", "倍数错误"),
    ROLE_IDENTIFYING("0018", "角色标识为空"),
    ROLE_NOT_EXIST("0019", "角色不存在"),
    PERMISSION_IDENTIFYING("0018", "权限参数错误"),
    PERMISSION_NOT_EXIST("0018", "权限不存在"),
    USER_NAME_NULL("0019", "用户名称为空"),
    CONVERT_PERCENT_NULL("0020", "转化率未配置"),
    CONVERT_LEVEL_NULL("0021", "充值积分规则未配置"),
    ID_NULL("0022", "Id 为空"),
    USERNAME_EXIST("0023", "登录名已存在"),
    NAME_NULL("0024", "名称为空"),
    PERMISSION_NAME_NULL("0025", "权限名称为空"),
    PERMISSION_NAME_EXIST("0026", "权限名称已存在"),
    USER_IS_LOCK("0027", "用户已锁定"),
    USER_PARENT_SELF("0028", "上级不能为自己"),
    USER_PARENT_CICLE("0029", "不能为循环上级"),
    RECHARGE_LIMIT("0030", "充值额不得小于一万"),
    UPDATE_ACCOUNT_LIMIT("0031", "积分修改额大于0"),
    SCORE_LESS_ERROR("0032", "现有积分不足"),
    ROLE_NAME_EXIST("0033", "角色名称已存在"),
    DOWNLOAD_OVERSTEP("0034", "导出数据过大"),
    AMOUNT_ERROR("0035", "金额错误"),
    USERLEVEL_IS_ERROR("0036", "用户等级错误"),
    USER_UPLEVEL_NOT_EXIST("0037", "用户升级金额未配置"),
    PERMISSION_DENY("0038","权限不足"),
    PERMISSION_LOCK("0039","权限锁定"),
    ROLE_LOCK("0040","角色锁定"),
    ADMIN_LOCK("0041","账户锁定"),
    USER_IS_SHOPKEEPER("0042","用户已是店主"),
    WITHDRAW_CONFIGURE_AMOUNT("0043","提现金额配置有误"),
    WITHDRAW_NOT_EXIST("0044","用户提现金额未配置"),
    WITHDRAW_AMOUNT("0045","提现金额有误"),
    BANK_CARD_ERROR("0046","银行卡有误"),
    BANK_CARD_EXIST("0047","银行卡已添加"),
    BANK_INFO_ERROR("0048","添加银行卡信息有误"),
    BALANCE_LESS_ERROR("0049","用户余额不足"),
    USER_NOT_BANKCARD("0050","用户未绑定银行卡"),
    PAYOUT_RECORD_ERROR("0051","用户提现异常"),
    TRADENO_ISEMPTY("0052","流水账号不能为空"),
    REJECT_REMARK_ISEMPTY("0053","拒绝原因不能为空"),
    BANK_ACCOUNT_USERNAME_ISEMPTY("0054","持卡人不能为空"),
    REPLENISHPERCENT_NULL("0055","补货率未配置"),
    TICKETPERCENT_NULL("0056","消费劵未配置"),
    REPLENISH_LIMIT("0057", "补货额不得小于1"),
    BITCOIN_LIMIT("0058", "充值额度错误"),
    NOT_ALLOW_GIVE_SELF("0059", "不可赠送给自己"),
    ADMIN_DEFAULT("0060", "初始管理员不可锁定"),
    IS_WEEKEND("0061", "周末才可提现"),
    ALIPAY_INFO_ERROR("0062","添加支付宝信息有误"),
    ALIPAY_EXIST("0063","支付宝账户已添加"),
    ALIPAY_NUMBER_NO_EXIST("0064","账户不能为空"),
    ALIPAY_NAME_NO_EXIST("0065","账户名不能为空"),
    USER_NOT_ALIPAY("0066","用户为绑定支付宝账户"),
    ILLEGAL_PARAMETER("0067","非法参数"),
    UNKNOW_ERROR("9999", "未知错误");
    private String code;
    private String name;

    private ErrorCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public  String  getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
