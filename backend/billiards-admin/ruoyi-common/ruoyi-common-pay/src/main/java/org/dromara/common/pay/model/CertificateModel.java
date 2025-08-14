package org.dromara.common.pay.model;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
public class CertificateModel {

    private static final long serialVersionUID = -3066491891078735673L;

    /**
     * 证书本身
     */
    private X509Certificate itself;

    /**
     * 版本号
     */
    private int version;

    /**
     * 证书序列号
     */
    private String serialNumber;

    /**
     * 签发者
     */
    private Principal issuerDn;

    /**
     * 主体名
     */
    private Principal subjectDn;

    /**
     * 有效起始日期
     */
    private Date notBefore;

    /**
     * 有效终止日期
     */
    private Date notAfter;


    public X509Certificate getItself() {
        return itself;
    }

    public void setItself(X509Certificate itself) {
        this.itself = itself;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Principal getIssuerDn() {
        return issuerDn;
    }

    public void setIssuerDn(Principal issuerDn) {
        this.issuerDn = issuerDn;
    }

    public Principal getSubjectDn() {
        return subjectDn;
    }

    public void setSubjectDn(Principal subjectDn) {
        this.subjectDn = subjectDn;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    @Override
    public String toString() {
        return "CertificateModel{" +
            "version='" + version + '\'' +
            ", serialNumber='" + serialNumber + '\'' +
            ", issuerDn=" + issuerDn +
            ", subjectDn=" + subjectDn +
            ", notBefore=" + notBefore +
            ", notAfter=" + notAfter +
            '}';
    }
}
