package org.dromara.common.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vincenty算法实现两点间距离计算
 * 该算法适用于地球椭球体模型，精度高于球面距离计算方法
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/5/26
 */
public class DistanceCalculateOfVincentyUtil {

    /** 长半径a=6378137米 (WGS-84椭球体) */
    private static final double a = 6378137.0;
    /** 短半径b=6356752.314245米 (WGS-84椭球体) */
    private static final double b = 6356752.314245;
    /** 扁率f=1/298.257223563 (WGS-84椭球体) */
    private static final double f = 1.0 / 298.257223563;
    /** 迭代次数限制 */
    private static final int MAX_ITERATIONS = 100;
    /** 收敛阈值 */
    private static final double CONVERGENCE_THRESHOLD = 1e-12;

    /**
     * 根据提供的经纬度计算两点间距离（米）
     *
     * @param lat1 坐标1纬度
     * @param lon1 坐标1经度
     * @param lat2 坐标2纬度
     * @param lon2 坐标2经度
     * @return 两点间距离（米）
     */
    public static double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // 参数校验
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 0.0;
        }
        
        // 如果两点坐标相同，直接返回0
        if (Math.abs(lat1 - lat2) < CONVERGENCE_THRESHOLD && Math.abs(lon1 - lon2) < CONVERGENCE_THRESHOLD) {
            return 0.0;
        }

        // 将角度转换为弧度
        double phi1 = Math.toRadians(lat1);
        double lambda1 = Math.toRadians(lon1);
        double phi2 = Math.toRadians(lat2);
        double lambda2 = Math.toRadians(lon2);
        
        // 计算reduced latitudes (使用椭球体模型)
        double U1 = Math.atan((1.0 - f) * Math.tan(phi1));
        double U2 = Math.atan((1.0 - f) * Math.tan(phi2));
        
        double sinU1 = Math.sin(U1);
        double cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2);
        double cosU2 = Math.cos(U2);
        
        // 初始经度差
        double L = lambda2 - lambda1;
        
        // 迭代变量
        double lambda = L;
        double sinLambda, cosLambda;
        double sinSigma, cosSigma, sigma;
        double sinAlpha, cosSqAlpha;
        double cos2SigmaM;
        
        int iterations = 0;
        
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            
            // 计算大圆距离角sigma
            sinSigma = Math.sqrt(
                Math.pow(cosU2 * sinLambda, 2) + 
                Math.pow(cosU1 * sinU2 - sinU1 * cosU2 * cosLambda, 2)
            );
            
            // 共点情况处理
            if (sinSigma == 0) {
                return 0.0;
            }
            
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            
            // 方位角
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            
            // 防止cosSqAlpha为0导致除零错误
            if (Math.abs(cosSqAlpha) < CONVERGENCE_THRESHOLD) {
                cos2SigmaM = 0;
            } else {
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            }
            
            // 迭代公式系数
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            
            // 保存上一次的lambda值
            double lambdaPrev = lambda;
            
            // 更新lambda值
            lambda = L + (1 - C) * f * sinAlpha * (
                sigma + C * sinSigma * (
                    cos2SigmaM + C * cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))
                )
            );
            
            iterations++;
            
            // 检查收敛条件
            if (Math.abs(lambda - lambdaPrev) <= CONVERGENCE_THRESHOLD || iterations >= MAX_ITERATIONS) {
                break;
            }
            
        } while (true);
        
        // 如果达到最大迭代次数仍未收敛，使用球面公式近似计算
        if (iterations >= MAX_ITERATIONS) {
            return calculateHaversineDistance(lat1, lon1, lat2, lon2);
        }
        
        // 计算椭球面距离
        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        
        double deltaSigma = B * sinSigma * (
            cos2SigmaM + B / 4 * (
                cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2)) -
                B / 6 * cos2SigmaM * (-3 + 4 * Math.pow(sinSigma, 2)) * (-3 + 4 * Math.pow(cos2SigmaM, 2))
            )
        );
        
        // 计算最终距离（米）
        double distance = b * A * (sigma - deltaSigma);
        
        // 返回距离，保留小数精度
        return new BigDecimal(distance)
            .setScale(3, RoundingMode.HALF_UP)
            .doubleValue();
    }

    /**
     * 使用Haversine公式快速计算球面距离（米）
     * 更适合对大量点进行快速筛选/排序，精度对附近搜索场景足够
     */
    public static double getDistanceHaversineMeters(double lat1, double lon1, double lat2, double lon2) {
        return calculateHaversineDistance(lat1, lon1, lat2, lon2);
    }

    /**
     * 计算给定中心点与半径（公里）的经纬度边界框
     * 返回数组: [minLat, maxLat, minLon, maxLon]
     */
    public static double[] boundingBox(double latDeg, double lonDeg, double radiusKm) {
        if (radiusKm <= 0) {
            return new double[] {latDeg, latDeg, lonDeg, lonDeg};
        }
        double latDelta = radiusKm / 111.32d; // 每度纬度约 111.32km
        double lonScale = Math.cos(Math.toRadians(latDeg));
        if (Math.abs(lonScale) < 1e-6) {
            lonScale = 1e-6;
        }
        double lonDelta = radiusKm / (111.32d * lonScale);
        double minLat = latDeg - latDelta;
        double maxLat = latDeg + latDelta;
        double minLon = lonDeg - lonDelta;
        double maxLon = lonDeg + lonDelta;
        return new double[] {minLat, maxLat, minLon, maxLon};
    }

    /**
     * 使用Haversine公式计算球面距离（作为备选方案）
     *
     * @param lat1 坐标1纬度
     * @param lon1 坐标1经度
     * @param lat2 坐标2纬度
     * @param lon2 坐标2经度
     * @return 两点间距离（米）
     */
    private static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // 地球平均半径（米）
        double radius = 6371000.0;
        
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                  Math.cos(phi1) * Math.cos(phi2) *
                  Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return radius * c;
    }

    /**
     * 根据提供的角度值，将其转化为弧度
     *
     * @param angle 角度值
     * @return 弧度值
     */
    public static double toRadians(Double angle) {
        if (angle == null) {
            return 0.0;
        }
        return Math.toRadians(angle);
    }
}
