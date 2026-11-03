package com.example.music.entity;

/**
 * 标签（歌单标签，用于分类与推荐）
 */
public class Tag {

    private Long id;
    private String name;      // 标签名：雨声、助眠、专注...
    private String category;  // 标签类别：自然声/场景/情绪
    private Integer useCount; // 被多少个歌单使用（用于标签云大小）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
}
