package org.example.peermatch.constant;

/**
 * 队伍状态枚举类
 *
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/14 15:34
 */
public enum TeamStatus {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私密"),
    SECRET(2, "加密");

    private int value;
    private String text;

    public static TeamStatus getTeamStatusByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TeamStatus teamStatus : TeamStatus.values()) {
            if (teamStatus.getValue() == value) {
                return teamStatus;
            }
        }
        return null;
    }

    TeamStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
