package com.app.chatserver.model;

import com.app.chatserver.enums.MediaType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private MediaType type;

    @Column(name = "size")
    private Long size;

    @Column(name = "path")
    private String path;
}
