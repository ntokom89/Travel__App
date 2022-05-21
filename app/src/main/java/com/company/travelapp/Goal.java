package com.company.travelapp;

public class Goal {

    private String goalID;
    private String goalDescription;
    private int goalTotalAmount;
    private int goalCurrentAmount;
    private String categoryID;

    public Goal(String goalID, String goalDescription, int goalTotalAmount, int goalCurrentAmount) {
        this.goalID = goalID;
        this.goalDescription = goalDescription;
        this.goalTotalAmount = goalTotalAmount;
        this.goalCurrentAmount = goalCurrentAmount;
    }

    public Goal() {

    }

    public String getGoalID() {
        return goalID;
    }

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public int getGoalTotalAmount() {
        return goalTotalAmount;
    }

    public void setGoalTotalAmount(int goalTotalAmount) {
        this.goalTotalAmount = goalTotalAmount;
    }

    public int getGoalCurrentAmount() {
        return goalCurrentAmount;
    }

    public void setGoalCurrentAmount(int goalCurrentAmount) {
        this.goalCurrentAmount = goalCurrentAmount;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
