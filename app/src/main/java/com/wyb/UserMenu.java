package com.wyb;

/**
 * Created by VIBHAV on 04-10-2017.
 */

public class UserMenu {
    String res_name;
    String food;
    int cost;
    int budget;
    int totalcost;
    int people;


    public UserMenu(String res_name, String food, int cost, int budget, int totalcost,int people) {
        this.res_name = res_name;
        this.food = food;
        this.cost = cost;
        this.budget = budget;
        this.totalcost = totalcost;
        this.people=people;
    }


    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(int totalcost) {
        this.totalcost = totalcost;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }
}
