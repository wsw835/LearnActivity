package com.wsw.actlearn.entity.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wsw
 * 流程定义展示信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefOptionVo implements Serializable {

    /**
     * 流程定义key
     */
    private String pdKey;

    /**
     * 流程定义名称
     */
    private String pdName;

}
