package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 职位表
 *
 * @TableName position
 */
@TableName(value = "position")
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Position implements Comparable<Position> {
    public static final String POSITION_KEY_ZHUGUAN = "supervisor";
    public static final String POSITION_KEY_JINGLI = "manager";
    public static final String POSITION_KEY_GAOJIJINGLI = "high_manager";
    public static final String POSITION_KEY_ZONGJIAN = "director";
    public static final String POSITION_KEY_ZONGCAI = "president";
    public static final String POSITION_KEY_FUZONGCAI = "vice_president";
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 英文标识
     */
    @EqualsAndHashCode.Include
    private String key;

    /**
     * 中文名称
     */
    private String name;

    /**
     * 级别，数字越大越高
     */
    private Integer level;

    /**
     * 比较两个职位高低
     * @param anotherPosition 用于比较的另一个职位
     * @return 1表示当前职位更高，-1表示当前职位更低，0表示相等
     */
    @Override
    public int compareTo(@NonNull Position anotherPosition) {
        if (this.level == null || anotherPosition.getLevel() == null) {
            throw new RuntimeException("缺少级别信息，不能比较");
        }
        if (this.level > anotherPosition.getLevel()) {
            return 1;
        }
        else if (this.level < anotherPosition.getLevel()) {
            return -1;
        }
        return 0;
    }
}