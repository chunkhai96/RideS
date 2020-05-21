package com.example.chunkhai.rides.Object;

public class User {
    private String user_uid;
    private String user_profileName;
    private String user_password;
    private String user_email;
    private String user_phoneNo;
    private String user_gender;
    private String user_signature;
    private String user_profilePicSrc;
    private long user_registerTimestamp;
    private String user_deviceToken;
    private String user_fullName;
    private String user_matrixNo;
    private String user_verifMatrixSrc;
    private int user_verifMatrixStatus;
    private long user_verifMatrixTimestamp;
    private String user_licenseNo;
    private String user_verifLicenseSrc;
    private int user_verifLicenseStatus;
    private long user_verifLicenseTimestamp;

    public User() {
    }

    public User(String user_profileName, String user_password,
                String user_email, String user_phoneNo, String user_gender, long user_registerTimestamp ) {
        this.user_profileName = user_profileName;
        this.user_password = user_password;
        this.user_email = user_email;
        this.user_phoneNo = user_phoneNo;
        this.user_gender = user_gender;
        this.user_registerTimestamp = user_registerTimestamp;
    }

    public User(String user_uid, String user_profileName, String user_password, String user_email, String user_phoneNo, String user_gender, String user_signature, String user_profilePicSrc, long user_registerTimestamp, String user_deviceToken, String user_fullName, String user_matrixNo, String user_verifMatrixSrc, int user_verifMatrixStatus, long user_verifMatrixTimestamp, String user_licenseNo, String user_verifLicenseSrc, int user_verifLicenseStatus, long user_verifLicenseTimestamp) {
        this.user_uid = user_uid;
        this.user_profileName = user_profileName;
        this.user_password = user_password;
        this.user_email = user_email;
        this.user_phoneNo = user_phoneNo;
        this.user_gender = user_gender;
        this.user_signature = user_signature;
        this.user_profilePicSrc = user_profilePicSrc;
        this.user_registerTimestamp = user_registerTimestamp;
        this.user_deviceToken = user_deviceToken;
        this.user_fullName = user_fullName;
        this.user_matrixNo = user_matrixNo;
        this.user_verifMatrixSrc = user_verifMatrixSrc;
        this.user_verifMatrixStatus = user_verifMatrixStatus;
        this.user_verifMatrixTimestamp = user_verifMatrixTimestamp;
        this.user_licenseNo = user_licenseNo;
        this.user_verifLicenseSrc = user_verifLicenseSrc;
        this.user_verifLicenseStatus = user_verifLicenseStatus;
        this.user_verifLicenseTimestamp = user_verifLicenseTimestamp;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_matrixNo() {
        return user_matrixNo;
    }

    public void setUser_matrixNo(String user_matrixNo) {
        this.user_matrixNo = user_matrixNo;
    }

    public String getUser_fullName() {
        return user_fullName;
    }

    public void setUser_fullName(String user_fullName) {
        this.user_fullName = user_fullName;
    }

    public String getUser_profileName() {
        return user_profileName;
    }

    public void setUser_profileName(String user_profileName) {
        this.user_profileName = user_profileName;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phoneNo() {
        return user_phoneNo;
    }

    public void setUser_phoneNo(String user_phoneNo) {
        this.user_phoneNo = user_phoneNo;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_signature() {
        return user_signature;
    }

    public void setUser_signature(String user_signature) {
        this.user_signature = user_signature;
    }

    public String getUser_profilePicSrc() {
        return user_profilePicSrc;
    }

    public void setUser_profilePicSrc(String user_profilePicSrc) {
        this.user_profilePicSrc = user_profilePicSrc;
    }

    public long getUser_registerTimestamp() {
        return user_registerTimestamp;
    }

    public void setUser_registerTimestamp(long user_registerTimestamp) {
        this.user_registerTimestamp = user_registerTimestamp;
    }

    public String getUser_deviceToken() {
        return user_deviceToken;
    }

    public void setUser_deviceToken(String user_deviceToken) {
        this.user_deviceToken = user_deviceToken;
    }

    public String getUser_verifMatrixSrc() {
        return user_verifMatrixSrc;
    }

    public void setUser_verifMatrixSrc(String user_verifMatrixSrc) {
        this.user_verifMatrixSrc = user_verifMatrixSrc;
    }

    public int getUser_verifMatrixStatus() {
        return user_verifMatrixStatus;
    }

    public void setUser_verifMatrixStatus(int user_verifMatrixStatus) {
        this.user_verifMatrixStatus = user_verifMatrixStatus;
    }

    public long getUser_verifMatrixTimestamp() {
        return user_verifMatrixTimestamp;
    }

    public void setUser_verifMatrixTimestamp(long user_verifMatrixTimestamp) {
        this.user_verifMatrixTimestamp = user_verifMatrixTimestamp;
    }

    public String getUser_licenseNo() {
        return user_licenseNo;
    }

    public void setUser_licenseNo(String user_licenseNo) {
        this.user_licenseNo = user_licenseNo;
    }

    public String getUser_verifLicenseSrc() {
        return user_verifLicenseSrc;
    }

    public void setUser_verifLicenseSrc(String user_verifLicenseSrc) {
        this.user_verifLicenseSrc = user_verifLicenseSrc;
    }

    public int getUser_verifLicenseStatus() {
        return user_verifLicenseStatus;
    }

    public void setUser_verifLicenseStatus(int user_verifLicenseStatus) {
        this.user_verifLicenseStatus = user_verifLicenseStatus;
    }

    public long getUser_verifLicenseTimestamp() {
        return user_verifLicenseTimestamp;
    }

    public void setUser_verifLicenseTimestamp(long user_verifLicenseTimestamp) {
        this.user_verifLicenseTimestamp = user_verifLicenseTimestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_matrixNo='" + user_matrixNo + '\'' +
                ", user_fullName='" + user_fullName + '\'' +
                ", user_profileName='" + user_profileName + '\'' +
                ", user_password='" + user_password + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_phoneNo='" + user_phoneNo + '\'' +
                ", user_gender='" + user_gender + '\'' +
                ", user_signature='" + user_signature + '\'' +
                ", user_profilePicSrc='" + user_profilePicSrc + '\'' +
                '}';
    }
}
