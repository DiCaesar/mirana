package mirana.demo.entity;

import mirana.demo.enumeration.Skill;

import java.util.List;

public class Cat {

    /**
     * 技巧
     */
    private Skill skill;

    /**
     * 名称
     */
    private String name;

    /**
     * 标签
     *
     * @remark 标签数量不会超过3个
     */
    private List<String> tags;

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
