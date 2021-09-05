package com.openld.planet.planet;

/**
 * author: lllddd
 * created on: 2021/9/4 12:37
 * description:3D点
 */
public class Point3DF {
    // x坐标
    public float x;
    // y坐标
    public float y;
    // z坐标
    public float z;

    public Point3DF(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3DF(Point3DF point3D) {
        this.x = point3D.x;
        this.y = point3D.y;
        this.z = point3D.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Point3DF point3D) {
        this.x = point3D.x;
        this.y = point3D.y;
        this.z = point3D.z;
    }

    public void negate() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
    }

    public void offset(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public boolean equals(float x, float y, float z) {
        return this.x == x && this.y == y && this.z == z;
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
