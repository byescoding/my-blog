package com.bai.blogadmin.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class simpleBlogListVo implements Serializable {
    private Long blogId;

    private String blogTitle;
}
