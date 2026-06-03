package org.example.admissionsecretary;

public class Applicant {
    private int id;
    private String fullName;
    private String email;
    private int listId;
    private int egeMath;
    private int egeRussian;
    private int egeSubject;
    private int achievementsScore;
    private int portfolioScore;
    private int totalScore;
    private boolean isTarget;
    private boolean isPriority;
    private String status;

    public Applicant() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public int getEgeMath() { return egeMath; }
    public void setEgeMath(int egeMath) { this.egeMath = egeMath; }

    public int getEgeRussian() { return egeRussian; }
    public void setEgeRussian(int egeRussian) { this.egeRussian = egeRussian; }

    public int getEgeSubject() { return egeSubject; }
    public void setEgeSubject(int egeSubject) { this.egeSubject = egeSubject; }

    public int getAchievementsScore() { return achievementsScore; }
    public void setAchievementsScore(int achievementsScore) { this.achievementsScore = achievementsScore; }

    public int getPortfolioScore() { return portfolioScore; }
    public void setPortfolioScore(int portfolioScore) { this.portfolioScore = portfolioScore; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public boolean isTarget() { return isTarget; }
    public void setTarget(boolean target) { isTarget = target; }

    public boolean isPriority() { return isPriority; }
    public void setPriority(boolean priority) { isPriority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
