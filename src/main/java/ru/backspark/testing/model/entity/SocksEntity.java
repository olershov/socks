package ru.backspark.testing.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_socks",
        uniqueConstraints = {@UniqueConstraint(columnNames =
                { "color", "cotton_percent" }) })
public class SocksEntity extends BaseEntity {

    @Column(name = "color")
    private String color;

    @Column(name = "count")
    private Integer count;

    @Column(name = "cotton_percent")
    private Integer cottonPercentContent;

    @Version
    private Long version;
}
