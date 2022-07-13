package com.qf.day74.entity;

import lombok.Data;

/**
 * json返回格式
 *
 * @author wangliang
 */
@Data
public class ResultData {
    /**
     * 查询结果编码，成功为10000，其他为失败
     */
    private String code;
    /**
     * 失败描述
     */
    private String desc;
    /**
     * 成功数据
     */
    private Object data;

    /**
     * 构造方法私有
     */
    public ResultData() {
    }

    /**
     * 创建成功信息
     *
     * @param data 成功数据
     * @return
     */
    public static ResultData createSuccessJsonResult(Object data) {
        ResultData vo = new ResultData();
        // 成功编码固定为10000
        vo.code = "10000";
        vo.data = data;
        return vo;
    }

    /**
     * 创建失败信息
     *
     * @param code 失败编码
     * @param desc 失败描述
     * @return
     */
    public static ResultData createFailJsonResult(String code, String desc) {
        ResultData vo = new ResultData();
        vo.code = code;
        vo.desc = desc;
        return vo;
    }
}