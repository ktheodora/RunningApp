package com.example.runningapp;

import android.support.annotation.NonNull;

import java.util.List;

public class User {

    private String userID, name, surname, password_raw, email;
    private float kg, weight, kmgoal_weekly, kmgoal_daily, kmgoal_monthly;

    public User (String UserID,String Password_raw, String Name, String Email,String Surname, float Kg, float Weight,float Km_W, float Km_D, float Km_M) {
        this.userID = UserID;
        this.password_raw = Password_raw;
        this.name = Name;
        this.surname = Surname;
        this.email = Email;
        this.kg = Kg;
        this.weight = Weight;
        this.kmgoal_weekly = Km_W;
        this.kmgoal_daily= Km_D;
        this.kmgoal_monthly = Km_M;
    }

    public User(){}

    public void update(String UserID,String Password_raw, String Name, String Email,String Surname, float Kg, float Weight,float Km_W, float Km_D, float Km_M) {
        this.userID = UserID;
        this.password_raw = Password_raw;
        this.name = Name;
        this.surname = Surname;
        this.email = Email;
        this.kg = Kg;
        this.weight = Weight;
        this.kmgoal_weekly = Km_W;
        this.kmgoal_daily= Km_D;
        this.kmgoal_monthly = Km_M;
    }

    @NonNull
    @Override
    public String toString() {
        return userID + " " + password_raw + " " + name + " " + surname + " " + email + " " + kg + " " + weight + " " + kmgoal_daily + " " + kmgoal_monthly + " " + kmgoal_weekly + " " ;
    }

    public void setUserID(String Username) {
        this.userID = Username;
    }
    public String getUserID() {
        return userID;
    }

    public void setPwd(String Password_raw) {
        this.password_raw = Password_raw;
    }
    public String getPwd() {
        return password_raw;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword_raw() {
        return password_raw;
    }

    public void setPassword_raw(String password_raw) {
        this.password_raw = password_raw;
    }

    public float getKg() {
        return kg;
    }

    public void setKg(float kg) {
        this.kg = kg;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getKmgoal_weekly() {
        return kmgoal_weekly;
    }

    public void setKmgoal_weekly(float kmgoal_weekly) {
        this.kmgoal_weekly = kmgoal_weekly;
    }

    public float getKmgoal_daily() {
        return kmgoal_daily;
    }

    public void setKmgoal_daily(float kmgoal_daily) {
        this.kmgoal_daily = kmgoal_daily;
    }

    public float getKmgoal_monthly() {
        return kmgoal_monthly;
    }

    public void setKmgoal_monthly(float kmgoal_monthly) {
        this.kmgoal_monthly = kmgoal_monthly;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }
    public String getEmail() {
        return email;
    }
}