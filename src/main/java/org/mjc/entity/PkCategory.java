package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("pk_category")
public class PkCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
