package com.example.dz_3_1;

import java.util.Calendar;

public class Human {
    public int id;
    public String firstName;
    public String lastName;
    public boolean gender;
    public Calendar birthDay;
    static int topId;

    public Human(String firstName, String lastName, boolean gender, Calendar birthDay) {
        topId++;
        id = topId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDay = birthDay;
    }
    public String getBirthDayString(){
        String str = "";
        int day = birthDay.get(Calendar.DAY_OF_MONTH);
        str += ((day < 10)?"0":"")+ day + "/";

        int mon = birthDay.get(Calendar.MONTH);
        str += ((mon < 10)?"0":"")+ mon + "/";

        str += birthDay.get(Calendar.YEAR);
        return str;
    }
    public  static Calendar makeCalendar(int day, int month, int year){
        Calendar C = Calendar.getInstance();
        C.setTimeInMillis(0);
        C.set(Calendar.YEAR, year);
        C.set(Calendar.MONTH, month);
        C.set(Calendar.DAY_OF_MONTH, day);
        return C;
    }
    public boolean isFieldEmpty(){
        if(firstName.isEmpty()||lastName.isEmpty())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return firstName + " "+
                lastName + " родился "+
                getBirthDayString();
    }
}
