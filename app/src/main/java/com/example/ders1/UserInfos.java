package com.example.ders1;

import java.io.Serializable;

public class UserInfos implements Serializable {
    public String name, password, studentNumber, email, dept, phone, degree, socialMedia, socialMedia2, userId;

    public  UserInfos(String name, String password, String studentNumber, String email, String dept, String phone, String degree){
        this.name = name;
        this.password = password;
        this.studentNumber = studentNumber;
        this.email = email;
        this.dept = dept;
        this.phone = phone;
        this.degree = degree;
    } //SignUp icin
    public UserInfos(String name, String password, String email, String dept, String phone){
        this.name = name;
        this.password = password;
        this.email = email;
        this.dept = dept;
        this.phone = phone;
    }
    public UserInfos(String userId, String name, String email, String studentNumber) { //userId bir column degil ondan dolayı hata verebilir
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.studentNumber = studentNumber;
    }

    // isValidEmail fonksiyonu
    public boolean isValidEmail(String email) {
        // Kabul edilebilir alan adı uzantıları için düzenli ifade
        String validDomainRegex = ".*@(std\\.yildiz\\.edu\\.tr|yildiz\\.edu\\.tr)$";

        // E-posta adresini belirli uzantılarla eşleştirmek için düzenli ifade
        return email.matches(validDomainRegex);
    }
    public UserInfos() {};

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }


    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getSocialMedia2() {
        return socialMedia2;
    }

    public void setSocialMedia2(String socialMedia2) {
        this.socialMedia2 = socialMedia2;
    }
}
