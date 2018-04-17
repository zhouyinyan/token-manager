package com.github.zhouyinyan;

import java.io.Serializable;

/**
 * Created by zhouyinyan on 2018/4/17.
 */
public class TestTokenInfo implements Serializable{

    private String id;

    private int f1;

    @Ignore
    private long f2;

    private Integer f3;

    private String f4;

    private double f5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public long getF2() {
        return f2;
    }

    public void setF2(long f2) {
        this.f2 = f2;
    }

    public Integer getF3() {
        return f3;
    }

    public void setF3(Integer f3) {
        this.f3 = f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public double getF5() {
        return f5;
    }

    public void setF5(double f5) {
        this.f5 = f5;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TestTokenInfo{");
        sb.append("id='").append(id).append('\'');
        sb.append(", f1=").append(f1);
        sb.append(", f2=").append(f2);
        sb.append(", f3=").append(f3);
        sb.append(", f4='").append(f4).append('\'');
        sb.append(", f5=").append(f5);
        sb.append('}');
        return sb.toString();
    }
}
