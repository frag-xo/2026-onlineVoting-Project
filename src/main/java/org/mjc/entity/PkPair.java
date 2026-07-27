package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("pk_pair")
public class PkPair implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String optionA;
    private String optionB;
    private Integer heat;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
