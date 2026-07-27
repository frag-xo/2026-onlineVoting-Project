package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("pk_battle")
public class PkBattle implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long pairId;
    private String chosen;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
