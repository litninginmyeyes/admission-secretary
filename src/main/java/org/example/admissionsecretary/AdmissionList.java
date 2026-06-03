package org.example.admissionsecretary;

public class AdmissionList {
    private int id;
    private String name;
    private String specialityName;
    private int totalPlaces;
    private int targetPlaces;
    private int priorityPlaces;

    public AdmissionList() {}

    public AdmissionList(String name, String specialityName, int totalPlaces, int targetPlaces, int priorityPlaces) {
        this.name = name;
        this.specialityName = specialityName;
        this.totalPlaces = totalPlaces;
        this.targetPlaces = targetPlaces;
        this.priorityPlaces = priorityPlaces;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialityName() { return specialityName; }
    public void setSpecialityName(String specialityName) { this.specialityName = specialityName; }

    public int getTotalPlaces() { return totalPlaces; }
    public void setTotalPlaces(int totalPlaces) { this.totalPlaces = totalPlaces; }

    public int getTargetPlaces() { return targetPlaces; }
    public void setTargetPlaces(int targetPlaces) { this.targetPlaces = targetPlaces; }

    public int getPriorityPlaces() { return priorityPlaces; }
    public void setPriorityPlaces(int priorityPlaces) { this.priorityPlaces = priorityPlaces; }

    @Override
    public String toString() {
        return name + " — " + specialityName;
    }
}
