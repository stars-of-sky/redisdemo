package com.rp.redisdemo.utils;

import javafx.geometry.Point2D;

/**
 * Create By yrp.
 * Date:2020/4/24
 */

public class GPSHelper {
    // 圆周率
    public static final double PI = 3.14159265358979324;
    // 赤道半径(单位m)
    private static final double EARTH_RADIUS = 6378137;

    /**
     * 转化为弧度(rad)
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,
     * 计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     * 西安 坐标:34°16′N 108°54′E﻿ / ﻿34.267°N 108.9° 北纬/东经
     * @param lon1 第一点的经度 longitude
     * @param lat1 第一点的纬度 latitude
     * @param lon2 第二点的经度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位km
     */
    public static double GetDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**(米勒投影算法）将经纬度转化为平面坐标 （单位m）
     * @param lon 经度    西经为负数
     * @param lat 维度    南纬是负数
     * @return 坐标
     * @Date 2018年8月24日
     */
    public static Point2D MillierConvertion(double lon, double lat)
    {
        double L = EARTH_RADIUS  * Math.PI * 2;//地球周长
        double W=L;// 平面展开后，x轴等于周长
        double H=L/2;// y轴约等于周长一半
        double mill=2.3;// 米勒投影中的一个常数，范围大约在正负2.3之间
        double x = lon * Math.PI / 180;// 将经度从度数转换为弧度
        double y = lat * Math.PI / 180;// 将纬度从度数转换为弧度
        y=1.25 * Math.log( Math.tan( 0.25 * Math.PI + 0.4 * y ) );// 米勒投影的转换
        // 弧度转为实际距离
        x = ( W / 2 ) + ( W / (2 * Math.PI) ) * x;
        y = ( H / 2 ) - ( H / ( 2 * mill ) ) * y;
//         double[] result=new double[2];
//         result[0]=x;
//         result[1]=y;
        return new Point2D(x, y);
    }

    /**
     * javafx.geometry.Point2D
     *     Point2D(x: double,y: double)		            //用给定的x和y坐标来创建一个Point2D对象
     *     distance(x: double,y: double): double		//返回该点到定点（x,y）的距离
     *     distance(p: Point2D): double		            //返回该点到p点的距离
     *     getX(): double				                //返回该点的x的坐标
     *     getY(): double				                //返回该点的y的坐标
     *     toString(): String			            	//返回该点的字符串表示
     */

    /**
     * 判断一个坐标是否在给定圆的内部
     *
     * @param circleX 圆心坐标X
     * @param circleY 圆心坐标Y
     * @param circleR 圆的半径
     * @param x       给定点的坐标X
     * @param y       给定点的坐标Y
     * @return true 如果坐标落在圆的内部， 否则返回false
     */
    public static boolean insideCircle(double circleX, double circleY, double circleR, double x, double y) {
        return Math.hypot(circleX - x, circleY - y) <= circleR;
    }

    public static void main(String[] args) {
        double s = GetDistance(34.33910446130852,108.9597624811217,34.51442598345254,109.51628071066324);
        double ss = GetDistance(108.856727,34.244233,108.858694,34.233587);
        System.out.println(s);
        System.out.println(ss);
        Point2D p1 = MillierConvertion(108.856727,34.244233);
        Point2D p2 = MillierConvertion(108.858694,34.233587);
        double distance = p1.distance(p2);
        System.out.println(distance);
    }
}

