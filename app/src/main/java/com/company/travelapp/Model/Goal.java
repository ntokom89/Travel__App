package com.company.travelapp.Model;

public class Goal {

    private String goalID;
    private String goalName;
    private String goalDescription;
    private int goalTotalAmount;
    private int goalCurrentAmount;
    private String categoryID;
    private String goalType;

    public Goal(String goalID, String goalDescription, int goalTotalAmount, int goalCurrentAmount) {
        this.goalID = goalID;
        this.goalDescription = goalDescription;
        this.goalTotalAmount = goalTotalAmount;
        this.goalCurrentAmount = goalCurrentAmount;
    }

    public Goal() {

    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public String getGoalID() {
        return goalID;
    }

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
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
