package aor.paj.dto;

import aor.paj.dto.Category;
import aor.paj.entity.CategoryEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private int totalUsers;
    private int confirmedUsers;
    private int unconfirmedUsers;
    private double averageTasksPerUser;
    private Map<String, Long> countTasksByState;

    private Map<String, Long> mostFrequentCategories;
    private double averageTaskCompletionTime;

    private Map<LocalDate, Integer> countUsersByRegistrationDate;
    private Map<LocalDate, Integer> countTaksByConclusionDate;

    public DashboardDTO() {
    }
    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getConfirmedUsers() {
        return confirmedUsers;
    }

    public void setConfirmedUsers(int confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    public int getUnconfirmedUsers() {
        return unconfirmedUsers;
    }

    public void setUnconfirmedUsers(int unconfirmedUsers) {
        this.unconfirmedUsers = unconfirmedUsers;
    }

    public double getAverageTasksPerUser() {
        return averageTasksPerUser;
    }

    public void setAverageTasksPerUser(double averageTasksPerUser) {
        this.averageTasksPerUser = averageTasksPerUser;
    }

    public Map<String, Long> getCountTasksByState() {
        return countTasksByState;
    }

    public void setCountTasksByState(Map<String, Long> countTasksByState) {
        this.countTasksByState = countTasksByState;
    }

    public Map<String, Long> getMostFrequentCategories() {
        return mostFrequentCategories;
    }

    public void setMostFrequentCategories(Map<String, Long> mostFrequentCategories) {
        this.mostFrequentCategories = mostFrequentCategories;
    }

    public double getAverageTaskCompletionTime() {
        return averageTaskCompletionTime;
    }

    public void setAverageTaskCompletionTime(double averageTaskCompletionTime) {
        this.averageTaskCompletionTime = averageTaskCompletionTime;
    }

    public Map<LocalDate, Integer> getCountUsersByRegistrationDate() {
        return countUsersByRegistrationDate;
    }

    public void setCountUsersByRegistrationDate(Map<LocalDate, Integer> countUsersByRegistrationDate) {
        this.countUsersByRegistrationDate = countUsersByRegistrationDate;
    }

    public Map<LocalDate, Integer> getCountTaksByConclusionDate() {
        return countTaksByConclusionDate;
    }

    public void setCountTaksByConclusionDate(Map<LocalDate, Integer> countTaksByConclusionDate) {
        this.countTaksByConclusionDate = countTaksByConclusionDate;
    }
}
